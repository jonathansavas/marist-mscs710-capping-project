package edu.marist.mscs710.persistenceapi;

import edu.marist.mscs710.metricscollector.metric.Fields;
import edu.marist.mscs710.metricscollector.metric.Metric;
import edu.marist.mscs710.metricscollector.metric.MetricType;
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
  private static String dbSchemaPath = "./src/main/resources/db_schema.sql";
  private static String dbFilePath = "./src/main/resources/test-metrics.db";

  private static MetricsPersistenceService sqlIte;

  private Metric metric = new Metric(MetricType.SYSTEM_METRICS, new HashMap<String, Object>() {{
    put(Fields.SystemMetrics.UPTIME.toString(), 999999);
    put(Fields.SystemMetrics.DATETIME.toString(), 111111);
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
  public void testPersistMetric() throws SQLException {
    sqlIte.persistMetric(metric);
    try (Connection conn = DriverManager.getConnection(SQLiteMetricsImpl.createSqliteDbUrl(dbFilePath))) {
      ResultSet rs = conn.createStatement()
        .executeQuery("SELECT * FROM " + metric.getMetricType().toString().toLowerCase());

      rs.next();

      Assert.assertEquals(999999, rs.getLong("uptime"));
      Assert.assertEquals(111111, rs.getLong("datetime"));
    }
  }

  @Test
  public void testGetMetricTypes() {
    Assert.assertTrue(sqlIte.getMetricTypes().size() > 0);
  }
}
