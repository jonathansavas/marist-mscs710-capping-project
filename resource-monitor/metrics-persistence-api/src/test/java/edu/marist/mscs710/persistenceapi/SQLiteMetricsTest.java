package edu.marist.mscs710.persistenceapi;

import edu.marist.mscs710.metricscollector.metric.Fields;
import edu.marist.mscs710.metricscollector.metric.Metric;
import edu.marist.mscs710.metricscollector.metric.MetricType;
import edu.marist.mscs710.metricscollector.system.Processes;
import edu.marist.mscs710.persistenceapi.db.SQLiteMetricsImpl;
import org.junit.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class SQLiteMetricsTest {
  private static String dbSchemaPath = "./src/test/resources/db_schema.sql";
  private static String dbFilePath = "./src/test/resources/metrics.db";

  private static MetricsPersistenceService sqlIte;

  private Metric metric = new Metric(MetricType.SYSTEM_METRICS, new HashMap<String, Object>() {{
    put(Fields.SystemMetrics.UPTIME.toString(), 999999L);
    put(Fields.SystemMetrics.DATETIME.toString(), 111111L);
  }});

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
  public void testCreateInsertStatement() {
    String expected = "INSERT INTO system_metrics (uptime,datetime) VALUES (999999,111111);";

    Assert.assertEquals(expected, SQLiteMetricsImpl.createSqlInsertStatement(metric));
  }

  @Test
  public void testPersistSystemMetrics() throws SQLException {
    sqlIte.persistMetric(metric);

    try (Connection conn = DriverManager.getConnection(SQLiteMetricsImpl.createSqliteDbUrl(dbFilePath))) {
      ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM " + metric.getMetricType().toString().toLowerCase() + ";");
      rs.next();

      Assert.assertEquals(999999, rs.getLong("uptime"));
      Assert.assertEquals(111111, rs.getLong("datetime"));
    }
  }

  @Test
  public void testGetMetricTypes() {
    Assert.assertTrue(sqlIte.getMetricTypes().size() > 0);
  }

  @Test
  public void testPersistProcess() throws SQLException {
    Metric process = new Metric(MetricType.PROCESSES, new HashMap<String, Object>() {{
      put(Fields.Processes.DATETIME.toString(), 1L);
      put(Fields.Processes.DELTA_MILLIS.toString(), 1L);
      put(Fields.Processes.PID.toString(), 1);
      put(Fields.Processes.NAME.toString(), "NAME");
      put(Fields.Processes.START_TIME.toString(), 1L);
      put(Fields.Processes.UPTIME.toString(), 1L);
      put(Fields.Processes.CPU_USAGE.toString(), 0.1);
      put(Fields.Processes.MEMORY.toString(), 1L);
      put(Fields.Processes.KB_READ.toString(), 1.0);
      put(Fields.Processes.KB_WRITTEN.toString(), 1.0);
      put(Fields.Processes.STATE.toString(), Processes.PidState.NEW.toString());
    }});

    sqlIte.persistMetric(process);

    try (Connection conn = DriverManager.getConnection(SQLiteMetricsImpl.createSqliteDbUrl(dbFilePath))) {
      ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM " + process.getMetricType().toString().toLowerCase() + ";");
      rs.next();

      Assert.assertEquals(1, rs.getLong("datetime"));
      Assert.assertEquals(1, rs.getLong("delta_millis"));
      Assert.assertEquals(1, rs.getInt("pid"));
      Assert.assertEquals("NAME", rs.getString("name"));
      Assert.assertEquals(1, rs.getLong("start_time"));
      Assert.assertEquals(1, rs.getLong("uptime"));
      Assert.assertEquals(0.1, rs.getDouble("cpu_usage"), 0.0);
      Assert.assertEquals(1, rs.getLong("memory"));
      Assert.assertEquals(1.0, rs.getDouble("kb_read"), 0.0);
      Assert.assertEquals(1.0, rs.getDouble("kb_written"), 0.0);
      Assert.assertEquals("NEW", rs.getString("state"));
    }
  }

  @Test
  public void testPersistCpu() throws SQLException {
    Metric cpu = new Metric(MetricType.CPU, new HashMap<String, Object>() {{
      put(Fields.Cpu.DATETIME.toString(), 1L);
      put(Fields.Cpu.DELTA_MILLIS.toString(), 1L);
      put(Fields.Cpu.UTILIZATION.toString(), 0.5);
      put(Fields.Cpu.TEMPERATURE.toString(), 0.0);
    }});

    sqlIte.persistMetric(cpu);

    try (Connection conn = DriverManager.getConnection(SQLiteMetricsImpl.createSqliteDbUrl(dbFilePath))) {
      ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM " + cpu.getMetricType().toString().toLowerCase() + ";");
      rs.next();

      Assert.assertEquals(1, rs.getLong("datetime"));
      Assert.assertEquals(1, rs.getLong("delta_millis"));
      Assert.assertEquals(0.5, rs.getDouble("utilization"), 0.0);
      Assert.assertEquals(0.0, rs.getDouble("temperature"), 0.0);
    }
  }

  @Test
  public void testPersistCpuCore() throws SQLException {
    Metric cpuCore = new Metric(MetricType.CPU_CORE, new HashMap<String, Object>() {{
      put(Fields.CpuCore.DATETIME.toString(), 1L);
      put(Fields.CpuCore.DELTA_MILLIS.toString(), 1L);
      put(Fields.CpuCore.CORE_ID.toString(), 1);
      put(Fields.CpuCore.CORE_UTILIZATION.toString(), 0.5);
    }});

    sqlIte.persistMetric(cpuCore);

    try (Connection conn = DriverManager.getConnection(SQLiteMetricsImpl.createSqliteDbUrl(dbFilePath))) {
      ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM " + cpuCore.getMetricType().toString().toLowerCase() + ";");
      rs.next();

      Assert.assertEquals(1, rs.getLong("datetime"));
      Assert.assertEquals(1, rs.getLong("delta_millis"));
      Assert.assertEquals(0.5, rs.getDouble("core_utilization"), 0.0);
      Assert.assertEquals(1, rs.getInt("core_id"));
    }
  }

  @Test
  public void testPersistMemory() throws SQLException {
    Metric memory = new Metric(MetricType.MEMORY, new HashMap<String, Object>() {{
      put(Fields.Memory.DATETIME.toString(), 1L);
      put(Fields.Memory.DELTA_MILLIS.toString(), 1L);
      put(Fields.Memory.PAGE_FAULTS.toString(), 1.0);
      put(Fields.Memory.UTILIZATION.toString(), 0.5);
    }});

    sqlIte.persistMetric(memory);

    try (Connection conn = DriverManager.getConnection(SQLiteMetricsImpl.createSqliteDbUrl(dbFilePath))) {
      ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM " + memory.getMetricType().toString().toLowerCase() + ";");
      rs.next();

      Assert.assertEquals(1, rs.getLong("datetime"));
      Assert.assertEquals(1, rs.getLong("delta_millis"));
      Assert.assertEquals(0.5, rs.getDouble("utilization"), 0.0);
      Assert.assertEquals(1.0, rs.getDouble("page_faults"), 0.0);
    }
  }

  @Test
  public void testPersistNetwork() throws SQLException {
    Metric network = new Metric(MetricType.NETWORK, new HashMap<String, Object>() {{
      put(Fields.Network.DATETIME.toString(), 1L);
      put(Fields.Network.DELTA_MILLIS.toString(), 1L);
      put(Fields.Network.THROUGHPUT.toString(), 1);
      put(Fields.Network.SEND.toString(), 1.0);
      put(Fields.Network.RECEIVE.toString(), 1.0);
    }});

    sqlIte.persistMetric(network);

    try (Connection conn = DriverManager.getConnection(SQLiteMetricsImpl.createSqliteDbUrl(dbFilePath))) {
      ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM " + network.getMetricType().toString().toLowerCase() + ";");
      rs.next();

      Assert.assertEquals(1, rs.getLong("datetime"));
      Assert.assertEquals(1, rs.getLong("delta_millis"));
      Assert.assertEquals(1, rs.getLong("throughput"));
      Assert.assertEquals(1.0, rs.getDouble("send"), 0.0);
      Assert.assertEquals(1.0, rs.getDouble("receive"), 0.0);
    }
  }

  @Test
  public void testPersistSystemConstants() throws SQLException {
    Metric systemConstants = new Metric(MetricType.SYSTEM_CONSTANTS, new HashMap<String, Object>() {{
      put(Fields.SystemConstants.TOTAL_MEMORY.toString(), 1.0);
      put(Fields.SystemConstants.PHYSICAL_CORES.toString(), 1);
      put(Fields.SystemConstants.LOGICAL_CORES.toString(), 1);
      put(Fields.SystemConstants.CPU_SPEED.toString(), 1.0);
    }});

    sqlIte.persistMetric(systemConstants);

    try (Connection conn = DriverManager.getConnection(SQLiteMetricsImpl.createSqliteDbUrl(dbFilePath))) {
      ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM " + systemConstants.getMetricType().toString().toLowerCase() + ";");
      rs.next();

      Assert.assertEquals(1.0, rs.getDouble("total_memory"), 0.0);
      Assert.assertEquals(1, rs.getInt("physical_cores"));
      Assert.assertEquals(1, rs.getInt("logical_cores"));
      Assert.assertEquals(1.0, rs.getDouble("cpu_speed"), 0.0);
    }
  }
}
