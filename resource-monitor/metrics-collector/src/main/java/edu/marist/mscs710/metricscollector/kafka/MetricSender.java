package edu.marist.mscs710.metricscollector.kafka;

import edu.marist.mscs710.metricscollector.Metric;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

/**
 * Wrapper to <tt>KafkaTemplate</tt>
 */
public class MetricSender {

  private KafkaTemplate<String, Metric> kafkaTemplate;

  /**
   * Constructs a new <tt>MetricSender</tt> configured to send messages to
   * the supplied Kafka brokers.
   *
   * @param kafkaBrokers list of Kafka brokers, in the form host:port
   */
  public MetricSender(List<String> kafkaBrokers) {
    kafkaTemplate = KafkaConfig.createKafkaTemplate(kafkaBrokers);
  }

  /**
   * Sends a <tt>Metric</tt> message to the specified topic
   *
   * @param topic  topic to which to send messages
   * @param metric data to send
   */
  public void send(String topic, Metric metric) {
    kafkaTemplate.send(topic, metric);
  }

  /**
   * Sends a <tt>Metric</tt> message to the specified topic with a given key.
   *
   * @param topic  topic to which to send messages
   * @param key    key for the message
   * @param metric data to send
   */
  public void send(String topic, String key, Metric metric) {
    kafkaTemplate.send(topic, key, metric);
  }

  /**
   * Flushes the producer send buffer.
   */
  public void flush() {
    kafkaTemplate.flush();
  }

  /**
   * Closes the producer.
   */
  public void close() {
    kafkaTemplate.flush();
    kafkaTemplate.getProducerFactory().reset();
  }
}
