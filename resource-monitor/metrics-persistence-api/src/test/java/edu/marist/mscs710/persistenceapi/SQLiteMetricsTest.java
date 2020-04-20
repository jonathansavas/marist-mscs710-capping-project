package edu.marist.mscs710.persistenceapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.marist.mscs710.metricscollector.Metric;
import edu.marist.mscs710.metricscollector.data.*;
import edu.marist.mscs710.metricscollector.metric.Fields;
import edu.marist.mscs710.metricscollector.system.Processes;
import edu.marist.mscs710.metricscollector.system.SystemConstants;
import edu.marist.mscs710.persistenceapi.db.SQLiteMetricsImpl;
import org.junit.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

public class SQLiteMetricsTest {
  private static String dbSchemaPath = "./src/test/resources/db_schema.sql";
  private static String dbFilePath = "./src/test/resources/metrics.db";

  private static final long ONE_MIN_MS = 1000 * 60;
  private static final long ONE_HOUR_MS = ONE_MIN_MS * 60;
  private static final long SQLITE_PRUNE_ELIGIBILITY_MS = ONE_HOUR_MS * 12;

  private static SQLiteMetricsImpl sqlIte;

  @BeforeClass
  public static void prepare() throws IOException, SQLException {
    sqlIte = new SQLiteMetricsImpl(dbFilePath, dbSchemaPath);
  }

  @AfterClass
  public static void cleanup() throws SQLException {
    List<String> tableNames = sqlIte.getMetricTypes();
    tableNames.add("prune_bounds");

    for (String table : tableNames) {
      try (Connection conn = DriverManager.getConnection(SQLiteMetricsImpl.createSqliteDbUrl(dbFilePath))) {
        conn.createStatement().execute("DROP TABLE " + table + ";");
      }
    }

  }

  @Test
  public void testPersistSystemMetrics() throws SQLException, JsonProcessingException {
    SystemData metric = RandomMetric.getRandomSystemData();

    sqlIte.persistMetric(metric);

    try (Connection conn = DriverManager.getConnection(SQLiteMetricsImpl.createSqliteDbUrl(dbFilePath))) {
      ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM " + Fields.METRIC_TYPE_SYSTEM_METRICS + ";");
      rs.next();

      Assert.assertEquals(metric.getUpTime(), rs.getLong(Fields.SYSTEM_METRICS_UPTIME));
      Assert.assertEquals(metric.getEpochMillisTime(), rs.getLong(Fields.SYSTEM_METRICS_DATETIME));
      Assert.assertEquals(metric.getDeltaMillis(), rs.getLong(Fields.SYSTEM_METRICS_DELTA_MILLIS));

      Assert.assertEquals(metric, sqlIte.createMetric(rs, Fields.METRIC_TYPE_SYSTEM_METRICS, SystemData.class));
    }
  }

  @Test
  public void testGetMetricTypes() {
    Assert.assertTrue(sqlIte.getMetricTypes().size() > 0);
  }

  @Test
  public void testPersistProcess() throws SQLException, JsonProcessingException {
    ProcessData process = RandomMetric.getRandomProcessData();

    sqlIte.persistMetric(process);

    try (Connection conn = DriverManager.getConnection(SQLiteMetricsImpl.createSqliteDbUrl(dbFilePath))) {
      ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM " + Fields.METRIC_TYPE_PROCESSES + ";");
      rs.next();

      Assert.assertEquals(process.getEpochMillisTime(), rs.getLong("datetime"));
      Assert.assertEquals(process.getDeltaMillis(), rs.getLong("delta_millis"));
      Assert.assertEquals(process.getPid(), rs.getInt("pid"));
      Assert.assertEquals(process.getName(), rs.getString("name"));
      Assert.assertEquals(process.getStartTime(), rs.getLong("start_time"));
      Assert.assertEquals(process.getUpTime(), rs.getLong("uptime"));
      Assert.assertEquals(process.getCpuUsage(), rs.getDouble("cpu_usage"), 0.0);
      Assert.assertEquals(process.getMemory(), rs.getLong("memory"));
      Assert.assertEquals(process.getKbRead(), rs.getDouble("kb_read"), 0.0);
      Assert.assertEquals(process.getKbWritten(), rs.getDouble("kb_written"), 0.0);
      Assert.assertEquals(process.getPidState(), Processes.PidState.valueOf(rs.getString("state")));

      Assert.assertEquals(process, sqlIte.createMetric(rs, Fields.METRIC_TYPE_PROCESSES, ProcessData.class));
    }
  }

  @Test
  public void testPersistCpu() throws SQLException, JsonProcessingException {
    CpuData cpu = RandomMetric.getRandomCpuData();

    sqlIte.persistMetric(cpu);

    try (Connection conn = DriverManager.getConnection(SQLiteMetricsImpl.createSqliteDbUrl(dbFilePath))) {
      ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM " + Fields.METRIC_TYPE_CPU + ";");
      rs.next();

      Assert.assertEquals(cpu.getEpochMillisTime(), rs.getLong("datetime"));
      Assert.assertEquals(cpu.getDeltaMillis(), rs.getLong("delta_millis"));
      Assert.assertEquals(cpu.getUtilization(), rs.getDouble("utilization"), 0.0);
      Assert.assertEquals(cpu.getTemperature(), rs.getDouble("temperature"), 0.0);

      Assert.assertEquals(cpu, sqlIte.createMetric(rs, Fields.METRIC_TYPE_CPU, CpuData.class));
    }
  }

  @Test
  public void testPersistCpuCore() throws SQLException, JsonProcessingException {
    CpuCoreData cpuCore = RandomMetric.getRandomCpuCoreData();

    sqlIte.persistMetric(cpuCore);

    try (Connection conn = DriverManager.getConnection(SQLiteMetricsImpl.createSqliteDbUrl(dbFilePath))) {
      ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM " + Fields.METRIC_TYPE_CPU_CORE + ";");
      rs.next();

      Assert.assertEquals(cpuCore.getEpochMillisTime(), rs.getLong("datetime"));
      Assert.assertEquals(cpuCore.getDeltaMillis(), rs.getLong("delta_millis"));
      Assert.assertEquals(cpuCore.getCoreUtilization(), rs.getDouble("core_utilization"), 0.0);
      Assert.assertEquals(cpuCore.getCoreId(), rs.getInt("core_id"));

      Assert.assertEquals(cpuCore, sqlIte.createMetric(rs, Fields.METRIC_TYPE_CPU_CORE, CpuCoreData.class));
    }
  }

  @Test
  public void testPersistMemory() throws SQLException, JsonProcessingException {
    MemoryData memory = RandomMetric.getRandomMemoryData();

    sqlIte.persistMetric(memory);

    try (Connection conn = DriverManager.getConnection(SQLiteMetricsImpl.createSqliteDbUrl(dbFilePath))) {
      ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM " + Fields.METRIC_TYPE_MEMORY + ";");
      rs.next();

      Assert.assertEquals(memory.getEpochMillisTime(), rs.getLong("datetime"));
      Assert.assertEquals(memory.getDeltaMillis(), rs.getLong("delta_millis"));
      Assert.assertEquals(memory.getMemoryUtilization(), rs.getDouble("utilization"), 0.0);
      Assert.assertEquals(memory.getPageFaults(), rs.getDouble("page_faults"), 0.0);

      Assert.assertEquals(memory, sqlIte.createMetric(rs, Fields.METRIC_TYPE_MEMORY, MemoryData.class));
    }
  }

  @Test
  public void testPersistNetwork() throws SQLException, JsonProcessingException {
    NetworkData network = RandomMetric.getRandomNetworkData();

    sqlIte.persistMetric(network);

    try (Connection conn = DriverManager.getConnection(SQLiteMetricsImpl.createSqliteDbUrl(dbFilePath))) {
      ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM " + Fields.METRIC_TYPE_NETWORK + ";");
      rs.next();

      Assert.assertEquals(network.getEpochMillisTime(), rs.getLong("datetime"));
      Assert.assertEquals(network.getDeltaMillis(), rs.getLong("delta_millis"));
      Assert.assertEquals(network.getThroughput(), rs.getLong("throughput"), 0.0);
      Assert.assertEquals(network.getSend(), rs.getDouble("send"), 0.0);
      Assert.assertEquals(network.getReceive(), rs.getDouble("receive"), 0);

      Assert.assertEquals(network, sqlIte.createMetric(rs, Fields.METRIC_TYPE_NETWORK, NetworkData.class));
    }
  }

  @Test
  public void testPersistSystemConstants() throws SQLException, JsonProcessingException {
    SystemConstants systemConstants = RandomMetric.getRandomSystemConstants();

    sqlIte.persistMetric(systemConstants);

    try (Connection conn = DriverManager.getConnection(SQLiteMetricsImpl.createSqliteDbUrl(dbFilePath))) {
      ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM " + Fields.METRIC_TYPE_SYSTEM_CONSTANTS + ";");
      rs.next();

      Assert.assertEquals(systemConstants.getTotalMemGb(), rs.getDouble("total_memory"), 0.0);
      Assert.assertEquals(systemConstants.getPhysicalCores(), rs.getInt("physical_cores"));
      Assert.assertEquals(systemConstants.getLogicalCores(), rs.getInt("logical_cores"));
      Assert.assertEquals(systemConstants.getCpuSpeed(), rs.getDouble("cpu_speed"), 0.0);

      Assert.assertEquals(systemConstants, sqlIte.createMetric(rs, Fields.METRIC_TYPE_SYSTEM_CONSTANTS, SystemConstants.class));
    }
  }

  @Test
  public void testGetMetricsInRange() throws SQLException {
    deleteAllRows(Fields.METRIC_TYPE_PROCESSES);

    ProcessData process = RandomMetric.getRandomProcessData();

    sqlIte.persistMetric(process);
    SQLiteMetricsImpl sqLiteMetrics = sqlIte;

    List<ProcessData> data = sqLiteMetrics.getMetricsInRange(0, process.getEpochMillisTime() + 1, Fields.METRIC_TYPE_PROCESSES, ProcessData.class);

    Assert.assertTrue(sqLiteMetrics.getMetricsInRange(0, process.getEpochMillisTime(), Fields.METRIC_TYPE_PROCESSES, ProcessData.class).isEmpty());

    Assert.assertEquals(process, data.get(0));
  }

  @Test
  public void testPrune() throws SQLException {
    deleteAllRows(Fields.METRIC_TYPE_SYSTEM_METRICS);

    long minuteBound = Instant.now().toEpochMilli() - SQLITE_PRUNE_ELIGIBILITY_MS;
    long hourBound = minuteBound - SQLITE_PRUNE_ELIGIBILITY_MS + 100;

    List<SystemData> metrics = Arrays.asList(
      new SystemData(4,4, hourBound - ONE_HOUR_MS * 3 / 2 ),
      new SystemData(4,4, hourBound - ONE_HOUR_MS * 3 / 2 + ONE_HOUR_MS - 1),
      new SystemData(1,1, hourBound - ONE_HOUR_MS * 3 / 2 + ONE_HOUR_MS),
      new SystemData(2,2, hourBound),
      new SystemData(2,2, hourBound + ONE_MIN_MS - 1),
      new SystemData(3,3, hourBound + ONE_MIN_MS),
      new SystemData(3,3, hourBound + 2 * ONE_MIN_MS - 1)
    );

    for (Metric metric : metrics) {
      sqlIte.persistMetric(metric);
    }

    sqlIte.prune();

    List<SystemData> hourlyPrune = sqlIte.getMetricsInRange(0, hourBound, Fields.METRIC_TYPE_SYSTEM_METRICS, SystemData.class);
    List<SystemData> minutelyPrune = sqlIte.getMetricsInRange(hourBound, minuteBound, Fields.METRIC_TYPE_SYSTEM_METRICS, SystemData.class);

    Assert.assertEquals(2, hourlyPrune.size());
    Assert.assertEquals(2, minutelyPrune.size());

    Assert.assertEquals(SystemData.combine(metrics.subList(0,2)), hourlyPrune.get(0));
    Assert.assertEquals(SystemData.combine(metrics.subList(2,3)), hourlyPrune.get(1));
    Assert.assertEquals(SystemData.combine(metrics.subList(3,5)), minutelyPrune.get(0));
    Assert.assertEquals(SystemData.combine(metrics.subList(5,7)), minutelyPrune.get(1));
  }

  private void deleteAllRows(String table) throws SQLException {
    try (Connection conn = DriverManager.getConnection(SQLiteMetricsImpl.createSqliteDbUrl(dbFilePath))) {
      conn.createStatement().executeUpdate("DELETE FROM " + table);
    }
  }
}
