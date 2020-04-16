package edu.marist.mscs710.persistenceapi;

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
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class SQLiteMetricsTest {
  private static String dbSchemaPath = "./src/test/resources/db_schema.sql";
  private static String dbFilePath = "./src/test/resources/metrics.db";

  private static Random ran = new Random();

  private static MetricsPersistenceService sqlIte;

  @BeforeClass
  public static void prepare() throws IOException, SQLException {
    sqlIte = new SQLiteMetricsImpl(dbFilePath, dbSchemaPath);
  }

  @AfterClass
  public static void cleanup() throws SQLException {
    List<String> tableNames = sqlIte.getMetricTypes();

    for (String table : tableNames) {
      try (Connection conn = DriverManager.getConnection(SQLiteMetricsImpl.createSqliteDbUrl(dbFilePath))) {
        conn.createStatement().execute("DROP TABLE " + table + ";");
      }
    }

  }

  @Test
  public void testPersistSystemMetrics() throws SQLException {
    SystemData metric = new SystemData(ran.nextLong(), ran.nextLong(), ran.nextLong());

    sqlIte.persistMetric(metric);

    try (Connection conn = DriverManager.getConnection(SQLiteMetricsImpl.createSqliteDbUrl(dbFilePath))) {
      ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM " + Fields.METRIC_TYPE_SYSTEM_METRICS + ";");
      rs.next();

      Assert.assertEquals(metric.getUpTime(), rs.getLong(Fields.SYSTEM_METRICS_UPTIME));
      Assert.assertEquals(metric.getEpochMillisTime(), rs.getLong(Fields.SYSTEM_METRICS_DATETIME));
    }
  }

  @Test
  public void testGetMetricTypes() {
    Assert.assertTrue(sqlIte.getMetricTypes().size() > 0);
  }

  @Test
  public void testPersistProcess() throws SQLException {
    ProcessData process = new ProcessData(ran.nextInt(), UUID.randomUUID().toString(), ran.nextLong(), ran.nextLong(), ran.nextDouble(),
      ran.nextLong(), ran.nextDouble(), ran.nextDouble(), Processes.PidState.RUNNING, ran.nextLong(), ran.nextLong());

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
    }
  }

  @Test
  public void testPersistCpu() throws SQLException {
    CpuData cpu = new CpuData(ran.nextDouble(), ran.nextDouble(), ran.nextLong(), ran.nextLong());

    sqlIte.persistMetric(cpu);

    try (Connection conn = DriverManager.getConnection(SQLiteMetricsImpl.createSqliteDbUrl(dbFilePath))) {
      ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM " + Fields.METRIC_TYPE_CPU + ";");
      rs.next();

      Assert.assertEquals(cpu.getEpochMillisTime(), rs.getLong("datetime"));
      Assert.assertEquals(cpu.getDeltaMillis(), rs.getLong("delta_millis"));
      Assert.assertEquals(cpu.getUtilization(), rs.getDouble("utilization"), 0.0);
      Assert.assertEquals(cpu.getTemperature(), rs.getDouble("temperature"), 0.0);
    }
  }

  @Test
  public void testPersistCpuCore() throws SQLException {
    CpuCoreData cpuCore = new CpuCoreData(ran.nextInt(), ran.nextDouble(), ran.nextLong(), ran.nextLong());

    sqlIte.persistMetric(cpuCore);

    try (Connection conn = DriverManager.getConnection(SQLiteMetricsImpl.createSqliteDbUrl(dbFilePath))) {
      ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM " + Fields.METRIC_TYPE_CPU_CORE + ";");
      rs.next();

      Assert.assertEquals(cpuCore.getEpochMillisTime(), rs.getLong("datetime"));
      Assert.assertEquals(cpuCore.getDeltaMillis(), rs.getLong("delta_millis"));
      Assert.assertEquals(cpuCore.getCoreUtilization(), rs.getDouble("core_utilization"), 0.0);
      Assert.assertEquals(cpuCore.getCoreId(), rs.getInt("core_id"));
    }
  }

  @Test
  public void testPersistMemory() throws SQLException {
    MemoryData memory = new MemoryData(ran.nextDouble(), ran.nextDouble(), ran.nextLong(), ran.nextLong());

    sqlIte.persistMetric(memory);

    try (Connection conn = DriverManager.getConnection(SQLiteMetricsImpl.createSqliteDbUrl(dbFilePath))) {
      ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM " + Fields.METRIC_TYPE_MEMORY + ";");
      rs.next();

      Assert.assertEquals(memory.getEpochMillisTime(), rs.getLong("datetime"));
      Assert.assertEquals(memory.getDeltaMillis(), rs.getLong("delta_millis"));
      Assert.assertEquals(memory.getMemoryUtilization(), rs.getDouble("utilization"), 0.0);
      Assert.assertEquals(memory.getPageFaults(), rs.getDouble("page_faults"), 0.0);
    }
  }

  @Test
  public void testPersistNetwork() throws SQLException {
    NetworkData network = new NetworkData(ran.nextDouble(), ran.nextDouble(), ran.nextLong(), ran.nextLong(), ran.nextLong());

    sqlIte.persistMetric(network);

    try (Connection conn = DriverManager.getConnection(SQLiteMetricsImpl.createSqliteDbUrl(dbFilePath))) {
      ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM " + Fields.METRIC_TYPE_NETWORK + ";");
      rs.next();

      Assert.assertEquals(network.getEpochMillisTime(), rs.getLong("datetime"));
      Assert.assertEquals(network.getDeltaMillis(), rs.getLong("delta_millis"));
      Assert.assertEquals(network.getThroughput(), rs.getLong("throughput"), 0.0);
      Assert.assertEquals(network.getSend(), rs.getDouble("send"), 0.0);
      Assert.assertEquals(network.getReceive(), rs.getDouble("receive"), 0);
    }
  }

  @Test
  public void testPersistSystemConstants() throws SQLException {
    SystemConstants systemConstants = new SystemConstants(ran.nextDouble(), ran.nextInt(), ran.nextInt(), ran.nextDouble());

    sqlIte.persistMetric(systemConstants);

    try (Connection conn = DriverManager.getConnection(SQLiteMetricsImpl.createSqliteDbUrl(dbFilePath))) {
      ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM " + Fields.METRIC_TYPE_SYSTEM_CONSTANTS + ";");
      rs.next();

      Assert.assertEquals(systemConstants.getTotalMemGb(), rs.getDouble("total_memory"), 0.0);
      Assert.assertEquals(systemConstants.getPhysicalCores(), rs.getInt("physical_cores"));
      Assert.assertEquals(systemConstants.getLogicalCores(), rs.getInt("logical_cores"));
      Assert.assertEquals(systemConstants.getCpuSpeed(), rs.getDouble("cpu_speed"), 0.0);
    }
  }
}
