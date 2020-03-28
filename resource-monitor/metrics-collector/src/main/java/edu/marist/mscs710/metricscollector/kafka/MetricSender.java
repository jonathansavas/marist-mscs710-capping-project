package edu.marist.mscs710.metricscollector.kafka;

import edu.marist.mscs710.metricscollector.metric.Metric;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

public class MetricSender {

  private KafkaTemplate<String, Metric> kafkaTemplate;

  public MetricSender(List<String> kafkaBrokers) {
    kafkaTemplate = KafkaConfig.createKafkaTemplate(kafkaBrokers);
  }

  public void send(String topic, Metric metric) {
    kafkaTemplate.send(topic, metric);
  }

  public void send(String topic, String key, Metric metric) {
    kafkaTemplate.send(topic, key, metric);
  }

  public void flush() {
    kafkaTemplate.flush();
  }
}
