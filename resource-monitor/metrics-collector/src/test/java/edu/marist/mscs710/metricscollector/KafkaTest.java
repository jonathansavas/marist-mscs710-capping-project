package edu.marist.mscs710.metricscollector;

import com.salesforce.kafka.test.KafkaTestUtils;
import com.salesforce.kafka.test.junit4.SharedKafkaTestResource;
import edu.marist.mscs710.metricscollector.kafka.KafkaConfig;
import edu.marist.mscs710.metricscollector.kafka.MetricDeserializer;
import edu.marist.mscs710.metricscollector.kafka.MetricSerializer;
import edu.marist.mscs710.metricscollector.metric.Fields;
import edu.marist.mscs710.metricscollector.metric.Metric;
import edu.marist.mscs710.metricscollector.metric.MetricType;
import edu.marist.mscs710.metricscollector.producer.OSMetricsProducer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.*;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
    Metric before = new Metric(MetricType.SYSTEM_METRICS, new HashMap<String, Object>() {{
      put(Fields.SystemMetrics.UPTIME.toString(), 999999);
      put(Fields.SystemMetrics.DATETIME.toString(), 111111);
    }});

    KafkaTestUtils k = kafka.getKafkaTestUtils();

    try (KafkaProducer<String, Metric> kafkaProducer =
           k.getKafkaProducer(StringSerializer.class, MetricSerializer.class)) {

      kafkaProducer.send(new ProducerRecord<>(topic, "", before));
      kafkaProducer.flush();
    }

    List<ConsumerRecord<String, Metric>> consumerRecords =
      k.consumeAllRecordsFromTopic(topic, StringDeserializer.class, MetricDeserializer.class);

    Assert.assertEquals(consumerRecords.size(), 1);

    Metric after = consumerRecords.get(0).value();

    Assert.assertEquals(before.getMetricType(), after.getMetricType());

    Assert.assertEquals(
      before.getMetricData().get(Fields.SystemMetrics.UPTIME.toString()),
      after.getMetricData().get(Fields.SystemMetrics.UPTIME.toString())
    );

    Assert.assertEquals(
      before.getMetricData().get(Fields.SystemMetrics.DATETIME.toString()),
      after.getMetricData().get(Fields.SystemMetrics.DATETIME.toString())
    );
  }

  @Test
  public void testProduceConsumeViaKafka() throws InterruptedException {
    String kafkaBroker = kafka.getKafkaConnectString();
    List<String> brokers = Collections.singletonList(kafkaBroker);
    MetricsProducer os = new OSMetricsProducer(brokers, topic);

    Map<MetricType, Set<String>> metricsFields = new HashMap<MetricType, Set<String>>() {
      {
        put(MetricType.CPU, toStringSet(Fields.Cpu.values()));
        put(MetricType.CPU_CORE, toStringSet(Fields.CpuCore.values()));
        put(MetricType.MEMORY, toStringSet(Fields.Memory.values()));
        put(MetricType.NETWORK, toStringSet(Fields.Network.values()));
        put(MetricType.PROCESSES, toStringSet(Fields.Processes.values()));
        put(MetricType.SYSTEM_CONSTANTS, toStringSet(Fields.SystemConstants.values()));
        put(MetricType.SYSTEM_METRICS, toStringSet(Fields.SystemMetrics.values()));
      }
    };

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
      Assert.assertNotSame(metric.getMetricType(), MetricType.NULL);
      MetricType type = metric.getMetricType();
      Map<String, Object> metricData = metric.getMetricData();
      Set<String> fields = metricsFields.get(type);

      for (String field : metricData.keySet()) {
        Assert.assertTrue(fields.contains(field));
      }
    }
  }

  @Test
  @Ignore
  public void entryPointTest() throws InterruptedException {
    String runFile = "src/test/runfile.tmp";
    System.setProperty("runfile", runFile);
    System.setProperty("kafkabroker", kafka.getKafkaConnectString());

    Thread collectorThread = new Thread(() -> {
      try {
        MetricsCollectorStarter.main(new String[0]);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

    collectorThread.start();

    Thread.sleep(15000);
    new File(runFile).delete();
    collectorThread.join();
  }


  public static <E extends Enum<E>> Set<String> toStringSet(E[] enumValues) {
    return Arrays.stream(enumValues)
      .map(Enum::toString)
      .collect(Collectors.toSet());
  }
}
