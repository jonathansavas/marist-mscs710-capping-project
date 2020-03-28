package edu.marist.mscs710.metricscollector.kafka;

import edu.marist.mscs710.metricscollector.metric.Metric;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Configuration class of Kafka producers and consumers.
 */
public class KafkaConfig {
  private static final long DEFAULT_IDLE_BETWEEN_POLLS = 500; // ms
  private static final OffsetResetPolicy DEFAULT_OFFSET_RESET_POLICY = OffsetResetPolicy.EARLIEST;

  /**
   * Enum of auto offset reset policies supported by Kafka. This policy determines
   * how a Kafka consumer behaves when there is no offset available. This primarily
   * affects the behavior of a new consumer to a topic.
   */
  public enum OffsetResetPolicy {
    /**
     * Read from the earliest available message on the topic.
     */
    EARLIEST, // We probably want this for the database.
    /**
     * Read from the latest available message.
     */
    LATEST,  // We probably want this for UI live data.
    /**
     * No policy. Consumers have to implement a custom policy, otherwise the
     * consumer throws an exception.
     */
    NONE
  }

  private static Map<String, Object> createProducerProps(List<String> kafkaBrokers) {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, String.join(",", kafkaBrokers));
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MetricSerializer.class);

    return props;
  }

  private static ProducerFactory<String, Metric> createProducerFactory(List<String> kafkaBrokers) {
    return new DefaultKafkaProducerFactory<>(createProducerProps(kafkaBrokers));
  }

  /**
   * Creates Kafka template configured to produce <tt>Metric</tt> messages to
   * the given list of Kafka brokers.
   * @param kafkaBrokers List of Kafka broker connection strings, in the form
   *                     of host:port
   * @return Kafka template configured to send <tt>Metric</tt> messages with
   * an optional <tt>String</tt> as a key.
   */
  public static KafkaTemplate<String, Metric> createKafkaTemplate(List<String> kafkaBrokers) {
    return new KafkaTemplate<>(createProducerFactory(kafkaBrokers));
  }

  private static Map<String, Object> createConsumerProps(List<String> kafkaBrokers, String consumerGroup,
                                                        OffsetResetPolicy resetPolicy) {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, String.join(",", kafkaBrokers));
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MetricDeserializer.class);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, resetPolicy.toString().toLowerCase());

    return props;
  }

  private static ConsumerFactory<String, Metric> createConsumerFactory(List<String> kafkaBrokers, String consumerGroup, OffsetResetPolicy resetPolicy) {
    return new DefaultKafkaConsumerFactory<>(createConsumerProps(kafkaBrokers, consumerGroup, resetPolicy));
  }

  /**
   * Creates a Kafka message listener with <tt>OffsetResetPolicy.EARLIEST</tt> and
   * idle between polls of 500 ms.
   * @param kafkaBrokers list of Kafka brokers
   * @param consumerGroup consumer group
   * @param topic topic from which to consume messages
   * @param dataConsumer <tt>Consumer</tt> functional interface, called on a
   *                     successful receive of a message
   * @return Kafka message listener
   */
  public static KafkaMessageListenerContainer<String, Metric> createListener(List<String> kafkaBrokers, String consumerGroup, String topic,
                                                                           Consumer<ConsumerRecord<String, Metric>> dataConsumer) {
    return KafkaConfig.createListener(kafkaBrokers, consumerGroup, DEFAULT_OFFSET_RESET_POLICY,
      topic, DEFAULT_IDLE_BETWEEN_POLLS, dataConsumer);
  }

  /**
   * Creates a Kafka message listener with the supplied parameters.
   * @param kafkaBrokers list of Kafka brokers
   * @param consumerGroup consumer group
   * @param resetPolicy consumer  auto offset reset policy
   * @param topic topic from which to consume messages
   * @param idleBetweenPolls number of milliseconds to wait between polls to get
   *                         messages from Kafka
   * @param dataConsumer <tt>Consumer</tt> functional interface, called on a
   *    *                     successful receive of a message
   * @return Kafka message listener
   */
  public static KafkaMessageListenerContainer<String, Metric> createListener(List<String> kafkaBrokers, String consumerGroup,
                                                                           OffsetResetPolicy resetPolicy, String topic, long idleBetweenPolls,
                                                                           Consumer<ConsumerRecord<String, Metric>> dataConsumer) {
    ContainerProperties containerProps = new ContainerProperties(topic);
    containerProps.setAckMode(ContainerProperties.AckMode.RECORD);
    containerProps.setMessageListener((MessageListener<String, Metric>) dataConsumer::accept);
    containerProps.setIdleBetweenPolls(idleBetweenPolls);

    return new KafkaMessageListenerContainer<>(createConsumerFactory(kafkaBrokers, consumerGroup, resetPolicy), containerProps);
  }
}
