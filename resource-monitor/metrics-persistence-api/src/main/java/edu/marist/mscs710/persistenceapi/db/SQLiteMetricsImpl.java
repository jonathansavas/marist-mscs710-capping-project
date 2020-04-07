package edu.marist.mscs710.persistenceapi.db;

import edu.marist.mscs710.metricscollector.metric.Metric;
import edu.marist.mscs710.metricscollector.utils.LoggerUtils;
import edu.marist.mscs710.persistenceapi.MetricsPersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SQLite implementation of <tt>MetricsPersistenceService</tt>. This class is
 * responsible for instantiating a SQLite database instance and persisting
 * <tt>Metric</tt> data objects.
 */
public class SQLiteMetricsImpl implements MetricsPersistenceService {
  private static final Logger LOGGER = LoggerFactory.getLogger(SQLiteMetricsImpl.class);

  private String dbUrl;
  private List<String> metricTypes;

  /**
   * Constructs a new <tt>SQLiteMetricsImpl</tt>, which will use an existing
   * SQLite db file, or create a new database if one does not exist at
   * "dbFilePath". Upon construction, this will also create/update the database
   * schema according to the ".sql" file at "dbSchemaPath".
   *
   * @param dbFilePath    path of the sqlite database file
   * @param dbSchemaPath  path of the sql database schema file
   * @throws SQLException
   * @throws IOException
   */
  public SQLiteMetricsImpl(String dbFilePath, String dbSchemaPath) throws SQLException, IOException {
    this.dbUrl = createSqliteDbUrl(dbFilePath);
    executeSqlScript(dbSchemaPath);
    setMetricTypes();
  }

  @Override
  public boolean persistMetric(Metric metric) {
    try (Connection conn = getSqliteConnection()) {
      conn.createStatement().execute(createSqlInsertStatement(metric));
    } catch (SQLException e) {
      LOGGER.error(LoggerUtils.getExceptionMessage(e));
      return false;
    }

    return true;
  }

  /**
   * Create an SQL INSERT statement for the supplied <tt>Metric</tt>.
   *
   * @param metric a <tt>Metric</tt> object
   * @return sql insert statement
   */
  public static String createSqlInsertStatement(Metric metric) {
    StringBuilder sqlInsert = new StringBuilder("INSERT INTO ")
      .append(metric.getMetricType().toString().toLowerCase())
      .append(" (");

    StringBuilder sqlValues = new StringBuilder(" VALUES (");

    for (Map.Entry<String, Object> entry : metric.getMetricData().entrySet()) {
      sqlInsert.append(entry.getKey().toLowerCase()).append(',');

      if (entry.getValue() instanceof String) {
        sqlValues.append('\"').append(entry.getValue()).append('\"');
      } else {
        sqlValues.append(entry.getValue());
      }

      sqlValues.append(',');
    }

    sqlInsert.deleteCharAt(sqlInsert.length() - 1);
    sqlValues.deleteCharAt(sqlValues.length() - 1);

    sqlInsert.append(')');
    sqlValues.append(");");

    return sqlInsert.append(sqlValues).toString();
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
      LOGGER.error(LoggerUtils.getExceptionMessage(e));
    }

    this.metricTypes = metricTypes.stream()
      .filter(s -> ! s.contains("sqlite"))
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
}
