package edu.marist.mscs710.persistenceapi;

import com.salesforce.kafka.test.KafkaTestUtils;
import com.salesforce.kafka.test.junit4.SharedKafkaTestResource;
import edu.marist.mscs710.metricscollector.data.SystemData;
import edu.marist.mscs710.metricscollector.kafka.MetricSerializer;
import edu.marist.mscs710.metricscollector.metric.Fields;
import edu.marist.mscs710.metricscollector.Metric;
import edu.marist.mscs710.persistenceapi.db.SQLiteMetricsImpl;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class MetricsPersistenceTest {
  private static String dbSchemaPath = "./src/test/resources/db_schema.sql";
  private static String dbFilePath = "./src/test/resources/metrics.db";

  private static MetricsPersistenceService sqlIte;

  @ClassRule
  public static final SharedKafkaTestResource kafka = new SharedKafkaTestResource();

  @BeforeClass
  public static void prepare() throws IOException, SQLException {
    sqlIte = new SQLiteMetricsImpl(dbFilePath, dbSchemaPath, false);
  }

  private String topic;

  @Before
  public void generateRandomTopic() {
    topic = "test-topic-embedded" + Instant.now().toEpochMilli();
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
  public void testKafkaListener() throws InterruptedException, SQLException, ExecutionException {
    List<Metric> metrics = LongStream.range(1, 6)
      .mapToObj(l -> createTestSystemMetric(l, l, l))
      .collect(Collectors.toList());

    KafkaTestUtils kTest = kafka.getKafkaTestUtils();

    try (KafkaProducer<String, Metric> kp =
           kTest.getKafkaProducer(StringSerializer.class, MetricSerializer.class)) {

      for (Metric metric : metrics) {
        kp.send(new ProducerRecord<>(topic, "", metric));
      }

      kp.flush();
    }

    String runFile = "./runfile.tmp";
    System.setProperty("kafkabroker", kafka.getKafkaConnectString());
    System.setProperty("metricstopic", topic);

    Thread consumerThread = new Thread(() -> MetricsPersistenceStarter.main(new String[0]));

    consumerThread.start();
    Thread.sleep(15000);
    new File(runFile).delete();
    consumerThread.join();

    try (Connection conn = DriverManager.getConnection(SQLiteMetricsImpl.createSqliteDbUrl(dbFilePath))) {
      ResultSet rs = conn.createStatement()
        .executeQuery("SELECT * FROM " + Fields.METRIC_TYPE_SYSTEM_METRICS + ";");

      for (int i = 0; i < metrics.size(); i++) {
        rs.next();

        Assert.assertEquals(i + 1, rs.getLong("uptime"));
        Assert.assertEquals(i + 1, rs.getLong("datetime"));
      }
    }

    try (AdminClient kafkaAdminClient = kTest.getAdminClient()) {
      long offset = kafkaAdminClient
        .listConsumerGroupOffsets("PERSISTENCE_SERVICE")
        .partitionsToOffsetAndMetadata()
        .get()
        .get(new TopicPartition(topic, 0))
        .offset();

      Assert.assertEquals(metrics.size(), offset);
    }
  }

  public static Metric createTestSystemMetric(long datetime, long uptime, long deltaMillis) {
    return new SystemData(uptime, deltaMillis, datetime);
  }
}
