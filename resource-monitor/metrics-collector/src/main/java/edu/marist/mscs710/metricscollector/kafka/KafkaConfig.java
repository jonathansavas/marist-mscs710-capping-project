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
import java.util.Properties;
import java.util.function.Consumer;

public class KafkaConfig {
  private static final long DEFAULT_IDLE_BETWEEN_POLLS = 500; // ms
  private static final OffsetResetPolicy DEFAULT_OFFSET_RESET_POLICY = OffsetResetPolicy.EARLIEST;

  public enum OffsetResetPolicy {
    EARLIEST, // When starting new consumer group, read from earliest available message. We probably want this for the database.
    LATEST,  // When starting new consumer group, read from latest available message. We probably want this for UI live data.
    NONE // Have to implement our own policy, otherwise will throw exception.
  }

  public static Map<String, Object> createProducerProps(List<String> kafkaBrokers) {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, String.join(",", kafkaBrokers));
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MetricSerializer.class);

    return props;
  }

  public static ProducerFactory<String, Metric> createProducerFactory(List<String> kafkaBrokers) {
    return new DefaultKafkaProducerFactory<>(createProducerProps(kafkaBrokers));
  }

  public static KafkaTemplate<String, Metric> createKafkaTemplate(List<String> kafkaBrokers) {
    return new KafkaTemplate<>(createProducerFactory(kafkaBrokers));
  }

  public static Map<String, Object> createConsumerProps(List<String> kafkaBrokers, String consumerGroup,
                                                        OffsetResetPolicy resetPolicy) {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, String.join(",", kafkaBrokers));
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MetricDeserializer.class);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, resetPolicy.toString().toLowerCase());

    return props;
  }

  public static ConsumerFactory<String, Metric> createConsumerFactory(List<String> kafkaBrokers, String consumerGroup, OffsetResetPolicy resetPolicy) {
    return new DefaultKafkaConsumerFactory<>(createConsumerProps(kafkaBrokers, consumerGroup, resetPolicy));
  }

  public static KafkaMessageListenerContainer<String, Metric> createListener(List<String> kafkaBrokers, String consumerGroup, String topic,
                                                                           Consumer<ConsumerRecord<String, Metric>> dataConsumer) {
    return KafkaConfig.createListener(kafkaBrokers, consumerGroup, DEFAULT_OFFSET_RESET_POLICY,
      topic, DEFAULT_IDLE_BETWEEN_POLLS, dataConsumer);
  }

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
