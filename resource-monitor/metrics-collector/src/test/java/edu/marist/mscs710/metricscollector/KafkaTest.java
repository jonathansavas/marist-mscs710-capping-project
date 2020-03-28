package edu.marist.mscs710.metricscollector;

import com.salesforce.kafka.test.junit4.SharedKafkaTestResource;
import edu.marist.mscs710.metricscollector.kafka.KafkaConfig;
import edu.marist.mscs710.metricscollector.metric.Fields;
import edu.marist.mscs710.metricscollector.metric.Metric;
import edu.marist.mscs710.metricscollector.metric.MetricType;
import edu.marist.mscs710.metricscollector.producer.OSMetricsProducer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;

import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class KafkaTest {

  @ClassRule
  public static final SharedKafkaTestResource kafka = new SharedKafkaTestResource();

  @Test
  public void testSerializeDeserialize() throws InterruptedException {
    String kafkaBroker = kafka.getKafkaConnectString();

    String topic = "test-topic-embedded";
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
      long endTime = Instant.now().toEpochMilli() + 1000 * 15;
      while (Instant.now().toEpochMilli() < endTime) {Thread.sleep(1000);}
    } finally {
      os.shutdown();
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


  public static <E extends Enum<E>> Set<String> toStringSet(E[] enumValues) {
    return Arrays.stream(enumValues)
      .map(Enum::toString)
      .collect(Collectors.toSet());
  }
}
