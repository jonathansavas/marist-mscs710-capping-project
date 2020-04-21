package edu.marist.mscs710.persistenceapi.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.marist.mscs710.metricscollector.Metric;
import edu.marist.mscs710.metricscollector.data.*;
import edu.marist.mscs710.metricscollector.kafka.MetricDeserializer;
import edu.marist.mscs710.metricscollector.metric.Fields;
import edu.marist.mscs710.persistenceapi.MetricsPersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static edu.marist.mscs710.persistenceapi.utils.DateUtils.convertEpochMillisDateFormat;

/**
 * SQLite implementation of <tt>MetricsPersistenceService</tt>. This class is
 * responsible for instantiating a SQLite database instance and persisting
 * <tt>Metric</tt> data objects.
 */
public class SQLiteMetricsImpl implements MetricsPersistenceService {
  private static final Logger LOGGER = LoggerFactory.getLogger(SQLiteMetricsImpl.class);

  private static final long ONE_MIN_MS = 1000 * 60;
  private static final long ONE_HOUR_MS = ONE_MIN_MS * 60;
  private static final long PRUNE_ELIGIBILITY_MS = ONE_HOUR_MS * 12;
  private static final String PRUNE_BOUND_TABLE = "prune_bounds";
  private static final String BOUND = "bound";

  private MetricDeserializer metricDeser = new MetricDeserializer();
  private ObjectMapper objectMapper = new ObjectMapper();

  private String dbUrl;
  private List<String> metricTypes;
  private List<String> prunables;

  private long nextPruneTime;
  private boolean autoPrune;

  /**
   * Constructs a new <tt>SQLiteMetricsImpl</tt>, which will use an existing
   * SQLite db file, or create a new database if one does not exist at
   * "dbFilePath". Upon construction, this will also create/update the database
   * schema according to the ".sql" file at "dbSchemaPath".
   *
   * @param dbFilePath    path of the sqlite database file
   * @param dbSchemaPath  path of the sql database schema file
   * @param autoPrune     if true, the database will prune itself every hour
   * @throws SQLException
   * @throws IOException
   */
  public SQLiteMetricsImpl(String dbFilePath, String dbSchemaPath, boolean autoPrune) throws SQLException, IOException {
    this.dbUrl = createSqliteDbUrl(dbFilePath);
    executeSqlScript(dbSchemaPath);
    createPruneBoundTable();
    setMetricTypes();

    prunables = metricTypes.stream()
      .filter(t -> ! t.equals(Fields.METRIC_TYPE_SYSTEM_CONSTANTS))
      .collect(Collectors.toList());

    this.autoPrune = autoPrune;

    LOGGER.info("SQLiteMetricsImpl created for db file path '{}' with 'autoPrune' = {}", dbFilePath, this.autoPrune);

    if (autoPrune)
      prune();
  }

  @Override
  public boolean persistMetric(Metric metric) {
    try (Connection conn = getSqliteConnection()) {
      conn.createStatement().execute(metric.toSqlInsertString());
    } catch (SQLException e) {
      LOGGER.error(e.getMessage(), e);
      return false;
    }

    if (autoPrune && Instant.now().toEpochMilli() > nextPruneTime)
      prune();

    return true;
  }

  private void persistMetric(Metric metric, Connection conn) throws SQLException {
    conn.createStatement().execute(metric.toSqlInsertString());
  }

  private void setMetricTypes() {
    List<String> metricTypes = new ArrayList<>();

    try (Connection conn = getSqliteConnection()) {
      ResultSet rs = conn.createStatement()
        .executeQuery("SELECT name FROM sqlite_master WHERE type='table'");

      while (rs.next()) {
        metricTypes.add(rs.getString("name"));
      }

    } catch (SQLException e) {
      LOGGER.error(e.getMessage(), e);
    }

    this.metricTypes = metricTypes.stream()
      .filter(s -> ! s.contains("sqlite"))
      .filter(s -> ! s.equals(PRUNE_BOUND_TABLE))
      .collect(Collectors.toList());
  }

  @Override
  public List<String> getMetricTypes() {
    return new ArrayList<>(metricTypes);
  }

  /**
   * Creates a connection String for the supplied SQLite database file.
   *
   * @param dbFilePath path of the SQLite database file
   * @return SQLite database connection String
   */
  public static String createSqliteDbUrl(String dbFilePath) {
    return "jdbc:sqlite:" + dbFilePath;
  }

  private Connection getSqliteConnection() throws SQLException {
    return DriverManager.getConnection(dbUrl);
  }

  // Statements that do not begin on a new line and in-line comments
  // will cause this method to fail
  private void executeSqlScript(String dbSchemaPath) throws IOException, SQLException {
    try (BufferedReader br = new BufferedReader(
      new InputStreamReader(new FileInputStream(dbSchemaPath)))) {

      StringBuilder sql = new StringBuilder();

      String line;

      while ((line = br.readLine()) != null) {
        if (line.startsWith("--"))
          continue;

        sql.append(line);

        if (line.endsWith(";")) {
          try (Connection conn = getSqliteConnection()) {
            conn.createStatement().execute(sql.toString());
          }
          sql = new StringBuilder();
        }
      }
    }
  }

  /**
   * Reduces the database size by combining metrics into snapshots of
   * greater time intervals. Records 12-24 hours old will be combined into
   * one minute snapshots, and any records older than 24 hours will be
   * combined into one hour snapshots.
   */
  public void prune() {
    LOGGER.info("Begin database pruning operation");

    long minuteBound = getPruneUpperBound();
    long hourBound = minuteBound - PRUNE_ELIGIBILITY_MS;

    for (String metricType : prunables) {
      long lowerBound = getLastPruneBound(metricType);

      // Lower bound is inclusive, upper bound is exclusive
      if (prune(lowerBound, hourBound, metricType, ONE_HOUR_MS)) {
        LOGGER.info("Successfully pruned \"{}\" metrics from {} to {} in 1 hour windows",
          metricType, convertEpochMillisDateFormat(lowerBound), convertEpochMillisDateFormat(hourBound));

        try {
          storeLastPruneBound(metricType, hourBound);
        } catch (SQLException e) {
          LOGGER.error("Failed to store prune boundary", e);
        }
      }

      if (prune(hourBound, minuteBound, metricType, ONE_MIN_MS)) {
        LOGGER.info("Successfully pruned \"{}\" metrics from {} to {} in 1 minute windows",
          metricType, convertEpochMillisDateFormat(hourBound), convertEpochMillisDateFormat(minuteBound));
      }
    }

    LOGGER.info("End database pruning operation");

    setNextPruneTime();
  }

  private boolean prune(long earliest, long latest, String metricType, long windowSize) {
    List<? extends MetricData> combinedMetrics = combineMetrics(earliest, latest, metricType, windowSize);

    if (combinedMetrics == null)
      return false;

    if (combinedMetrics.isEmpty())
      return true;

    Connection conn = null;
    try {
      conn = getSqliteConnection();
      conn.setAutoCommit(false);

      deleteRecordsInRange(earliest, latest, metricType, conn);

      for (MetricData metric : combinedMetrics) {
        persistMetric(metric, conn);
      }

      conn.commit();
    } catch (SQLException e1) {
      LOGGER.error("Failed prune for \"{}\" metrics from {} to {}, rolling back changes",
        metricType, convertEpochMillisDateFormat(earliest), convertEpochMillisDateFormat(latest), e1);

      try {
        if (conn != null)
          conn.rollback();
      } catch (SQLException e2) {
        LOGGER.error("Rollback failed", e2);
      }

      return false;
    } finally {
      try {
        if (conn != null)
          conn.close();
      } catch (SQLException e3) {
        LOGGER.error("Failed to close SQLite connection", e3);
      }
    }

    return true;
  }

  private List<? extends MetricData> combineMetrics(long earliest, long latest, String metricType, long windowSize) {
    // Bucket metrics into windowSize (ms) buckets, combine each bucket, return all in a list
    switch (metricType) {
      case (Fields.METRIC_TYPE_CPU):
        List<List<CpuData>> cpuMetrics = bucketMetrics(windowSize, getMetricsInRange(earliest, latest, CpuData.class));
        return cpuMetrics == null ? null : cpuMetrics.stream().map(CpuData::combine).collect(Collectors.toList());

      case (Fields.METRIC_TYPE_CPU_CORE):
        List<List<CpuCoreData>> cpuCoreMetrics = bucketMetrics(windowSize, getMetricsInRange(earliest, latest, CpuCoreData.class));
        return cpuCoreMetrics == null ? null : cpuCoreMetrics.stream().map(CpuCoreData::combine).flatMap(Collection::stream).collect(Collectors.toList());

      case (Fields.METRIC_TYPE_MEMORY):
        List<List<MemoryData>> memoryMetrics = bucketMetrics(windowSize, getMetricsInRange(earliest, latest, MemoryData.class));
        return memoryMetrics == null ? null : memoryMetrics.stream().map(MemoryData::combine).collect(Collectors.toList());

      case (Fields.METRIC_TYPE_NETWORK):
        List<List<NetworkData>> networkMetrics = bucketMetrics(windowSize, getMetricsInRange(earliest, latest, NetworkData.class));
        return networkMetrics == null ? null : networkMetrics.stream().map(NetworkData::combine).collect(Collectors.toList());

      case (Fields.METRIC_TYPE_PROCESSES):
        List<List<ProcessData>> processMetrics = bucketMetrics(windowSize, getMetricsInRange(earliest, latest, ProcessData.class));
        return processMetrics == null ? null : processMetrics.stream().map(ProcessData::combine).flatMap(Collection::stream).collect(Collectors.toList());

      case (Fields.METRIC_TYPE_SYSTEM_METRICS):
        List<List<SystemData>> systemMetrics = bucketMetrics(windowSize, getMetricsInRange(earliest, latest, SystemData.class));
        return systemMetrics == null ? null : systemMetrics.stream().map(SystemData::combine).collect(Collectors.toList());

      default:
        return null;
    }
  }

  /**
   * Groups metrics based on spans of time in milliseconds. All metrics that
   * fall into each time bucket will be grouped together, regardless of the
   * number of instances. The buckets will begin with the minimum timestamp
   * in the list of metrics: [min,min+bucketSize), [min+bucketSize,min+2*bucketSize) ...
   *
   * @param bucketSize number of milliseconds in each time span
   * @param metrics    list of metrics
   * @param <T>        <tt>MetricData</tt> or its subtypes
   * @return list of lists of metrics, grouped by bucketSize, or null if
   *         <tt>metrics</tt> is null.
   */
  public static <T extends MetricData> List<List<T>> bucketMetrics(long bucketSize, List<T> metrics) {
    if (metrics == null)
      return null;

    if (metrics.isEmpty())
      return new ArrayList<>();

    long min = metrics.stream().min(Comparator.comparing(MetricData::getEpochMillisTime)).get().getEpochMillisTime();

    // Group in buckets of [min,min+bucketSize), [min+bucketSize,min+2*bucketSize) ...
    return new ArrayList<>(metrics.stream()
      .collect(Collectors.groupingBy(m -> (m.getEpochMillisTime() - min) / bucketSize, Collectors.toList()))
      .values());
  }

  @Override
  public <T extends MetricData> List<T> getMetricsInRange(long earliest, long latest, Class<T> clazz) {
    String metricType = MetricDeserializer.lookupMetricType(clazz);
    try (Connection conn = getSqliteConnection()) {
      ResultSet rs = getRecordsInRange(earliest, latest, metricType, conn);

      List<T> metrics = new ArrayList<>();

      while (rs.next()) {

        try {
          metrics.add(createMetric(rs, clazz));
        } catch (JsonProcessingException e) {
          LOGGER.error(e.getMessage(), e);
          return null;
        }
      }

      return metrics;
    } catch (SQLException e) {
      LOGGER.error(e.getMessage(), e);
      return null;
    }
  }

  /**
   * Creates a <tt>Metric</tt> object from the current row of the supplied
   * <tt>ResultSet</tt>. This method does not advance the cursor.
   *
   * @param rs         <tt>ResultSet</tt> from an SQL query
   * @param clazz      class corresponding to <tt>metricType</tt> to hold the returned metric data
   * @param <T>        <tt>Metric</tt> and its subtypes
   * @return metric data of type <tt>T</tt>
   * @throws SQLException
   * @throws JsonProcessingException
   */
  public <T extends Metric> T createMetric(ResultSet rs, Class<T> clazz) throws SQLException, JsonProcessingException {
    ObjectNode node = objectMapper.createObjectNode();

    ResultSetMetaData rsmd = rs.getMetaData();
    int numColumns = rsmd.getColumnCount();

    for (int i = 1; i <= numColumns; i++) {
      switch (rsmd.getColumnType(i)) {
        case (java.sql.Types.BIGINT):
          node.put(rsmd.getColumnName(i), rs.getLong(i));
          break;

        case (java.sql.Types.INTEGER):
        case (java.sql.Types.SMALLINT):
        case (java.sql.Types.TINYINT):
          node.put(rsmd.getColumnName(i), rs.getInt(i));
          break;

        case (java.sql.Types.REAL):
        case (java.sql.Types.DOUBLE):
        case (java.sql.Types.FLOAT):
          node.put(rsmd.getColumnName(i), rs.getDouble(i));
          break;

        case (java.sql.Types.VARCHAR):
        case (java.sql.Types.NVARCHAR):
        case (java.sql.Types.LONGVARCHAR):
        case (java.sql.Types.LONGNVARCHAR):
        case (java.sql.Types.CHAR):
        case (java.sql.Types.NCHAR):
          node.put(rsmd.getColumnName(i), rs.getString(i));
          break;

        default:
          node.putPOJO(rsmd.getColumnName(i), rs.getObject(i));
          break;
      }
    }

    return metricDeser.deserialize(node, clazz);
  }

  private static ResultSet getRecordsInRange(long earliest, long latest, String table, Connection conn) throws SQLException {
    // Earliest is inclusive and latest is exclusive
    return conn.createStatement()
      .executeQuery(
        "SELECT * FROM " + table +
        " WHERE datetime BETWEEN " + earliest + " AND " + (latest - 1)
      );
  }

  private void deleteRecordsInRange(long earliest, long latest, String table, Connection conn) throws SQLException {
    // Earliest is inclusive, latest is exclusive
    conn.createStatement()
      .executeUpdate(
        "DELETE FROM " + table +
          " WHERE datetime BETWEEN " + earliest + " AND " + (latest - 1)
      );
  }

  private void storeLastPruneBound(String metricType, long exclusiveBound) throws SQLException {
    try (Connection conn = getSqliteConnection()) {
      conn.createStatement().execute(
        "REPLACE INTO " + PRUNE_BOUND_TABLE +
          " (" + Fields.METRIC_TYPE + ',' + BOUND + ") VALUES (" +
          '\'' + metricType + '\'' + ',' + exclusiveBound + ')' + ';'
      );
    }
  }

  private long getLastPruneBound(String metricType) {
    try (Connection conn = getSqliteConnection()) {
      ResultSet rs = conn.createStatement().executeQuery(
        "SELECT * FROM " + PRUNE_BOUND_TABLE +
          " WHERE " + Fields.METRIC_TYPE + " = '" + metricType + '\''
      );

      if (rs.next())
        return rs.getLong(BOUND);
      else
        return 0;
    } catch (SQLException ex) {
      LOGGER.error("Error retrieving boundary for pruning", ex);
      return 0;
    }
  }

  private void createPruneBoundTable() throws SQLException {
    try (Connection conn = getSqliteConnection()) {
      conn.createStatement().execute(
        "CREATE TABLE IF NOT EXISTS " + PRUNE_BOUND_TABLE + " ( " +
          Fields.METRIC_TYPE + " TEXT NOT NULL PRIMARY KEY, " +
          BOUND + " BIGINT NOT NULL);"
      );
    }
  }

  private static long getPruneUpperBound() {
    // Exclusive upper bound
    return Instant.now().toEpochMilli() - PRUNE_ELIGIBILITY_MS;
  }

  private void setNextPruneTime() {
    nextPruneTime = Instant.now().toEpochMilli() + ONE_HOUR_MS;
  }

}
