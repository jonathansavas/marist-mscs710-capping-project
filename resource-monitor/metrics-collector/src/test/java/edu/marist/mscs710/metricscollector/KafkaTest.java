package edu.marist.mscs710.metricscollector;

import com.salesforce.kafka.test.KafkaTestUtils;
import com.salesforce.kafka.test.junit4.SharedKafkaTestResource;
import edu.marist.mscs710.metricscollector.data.CpuCoreData;
import edu.marist.mscs710.metricscollector.kafka.KafkaConfig;
import edu.marist.mscs710.metricscollector.kafka.MetricDeserializer;
import edu.marist.mscs710.metricscollector.kafka.MetricSerializer;
import edu.marist.mscs710.metricscollector.metric.NullMetric;
import edu.marist.mscs710.metricscollector.producer.OSMetricsProducer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.*;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;

import java.io.File;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

public class KafkaTest {

  @ClassRule
  public static final SharedKafkaTestResource kafka = new SharedKafkaTestResource();

  private String topic;

  @Before
  public void generateRandomTopic() {
    topic = "test-topic-embedded" + Instant.now().toEpochMilli();
  }

  @Test
  public void testMetricSerializerDeserializerViaKafka() {
    CpuCoreData before = new CpuCoreData(Integer.MAX_VALUE, Double.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE);

    KafkaTestUtils k = kafka.getKafkaTestUtils();

    try (KafkaProducer<String, Metric> kafkaProducer =
           k.getKafkaProducer(StringSerializer.class, MetricSerializer.class)) {

      kafkaProducer.send(new ProducerRecord<>(topic, "", before));
      kafkaProducer.flush();
    }

    List<ConsumerRecord<String, Metric>> consumerRecords =
      k.consumeAllRecordsFromTopic(topic, StringDeserializer.class, MetricDeserializer.class);

    Assert.assertEquals(consumerRecords.size(), 1);

    CpuCoreData after = (CpuCoreData) consumerRecords.get(0).value();

    Assert.assertEquals(before.getEpochMillisTime(), after.getEpochMillisTime());
    Assert.assertEquals(before.getDeltaMillis(), after.getDeltaMillis());
    Assert.assertEquals(before.getCoreUtilization(), after.getCoreUtilization(), 0.0);
    Assert.assertEquals(before.getCoreId(), after.getCoreId());
  }

  @Test
  public void testProduceConsumeViaKafka() throws InterruptedException {
    String kafkaBroker = kafka.getKafkaConnectString();
    List<String> brokers = Collections.singletonList(kafkaBroker);
    MetricsProducer os = new OSMetricsProducer(brokers, topic);

    List<Metric> metrics = new ArrayList<>();
    Consumer<ConsumerRecord<String, Metric>> dataConsumer = (m) -> metrics.add(m.value());

    KafkaMessageListenerContainer<String, Metric> listener =
      KafkaConfig.createListener(brokers, "TEST_GROUP", topic, dataConsumer);

    try {
      os.start();
      listener.start();
      long endTime = Instant.now().toEpochMilli() + 1000 * 10;
      while (Instant.now().toEpochMilli() < endTime) {
        Thread.sleep(1000);
      }
    } finally {
      os.shutdown();
      Thread.sleep(1000);
      listener.stop();
    }

    Assert.assertTrue(metrics.size() > 0);

    for (Metric metric : metrics) {
      Assert.assertFalse(metric instanceof NullMetric);
    }
  }

  @Test
  @Ignore
  public void entryPointTest() throws InterruptedException {
    String runFile = "./runfile.tmp";
    System.setProperty("kafkabroker", kafka.getKafkaConnectString());

    Thread collectorThread = new Thread(() -> MetricsCollectorStarter.main(new String[0]));

    collectorThread.start();

    Thread.sleep(15000);
    new File(runFile).delete();
    collectorThread.join();
  }
}
