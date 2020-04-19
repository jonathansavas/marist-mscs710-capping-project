package edu.marist.mscs710.metricscollector;

import edu.marist.mscs710.metricscollector.producer.OSMetricsProducer;;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.errors.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Starts the metrics collection process. This class is responsible for
 * managing the lifecycle of a <tt>MetricsProducer</tt>.
 */
public class MetricsCollectorStarter {
  private static final Logger LOGGER = LoggerFactory.getLogger(MetricsCollectorStarter.class);

  private static final String PROPERTIES_FILE = "application.properties";
  private static final int REQUEST_TIMEOUT_MS = 1000 * 30;
  private static final int RUNFILE_CHECK_INTERVAL_MS = 1000 * 2;
  private static final String DEFAULT_TOPIC = "resource-monitor-metrics";
  private static final String DEFAULT_BROKER = "localhost:9092";
  private static final String RUNFILE = "./runfile.tmp";
  private static final int LOG_RETENTION_HOURS = 2;

  public static void main(String[] args) {
    Properties appProps = new Properties();

    try (InputStream propsStream = MetricsCollectorStarter.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
      if (propsStream == null)
        LOGGER.error("Could not locate \"{}\" on the classpath", PROPERTIES_FILE);
      else
        appProps.load(propsStream);
    } catch (IOException ex) {
      LOGGER.error(ex.getMessage(), ex);
    }

    // Command-line properties should take precedence over properties file.
    appProps.putAll(System.getProperties());

    String kafkaBroker = appProps.getProperty("kafkabroker", DEFAULT_BROKER);
    String topic = appProps.getProperty("metricstopic", DEFAULT_TOPIC);

    try (AdminClient kafkaAdminClient = createKafkaAdminClient(kafkaBroker)) {
      // Test broker connection and create topic if necessary
      if (!kafkaAdminClient.listTopics().names().get().contains(topic)) {
        createTopic(kafkaAdminClient, topic);
      }
    } catch (TimeoutException | InterruptedException | ExecutionException e) {
      LOGGER.error(e.getMessage(), e);
      return;
    }

    File runFile = new File(RUNFILE);

    try {
      runFile.createNewFile();
    } catch (IOException ex) {
      LOGGER.error(ex.getMessage(), ex);
      return;
    }

    runFile.deleteOnExit();

    MetricsProducer metricsProducer = new OSMetricsProducer(Collections.singletonList(kafkaBroker), topic);
    metricsProducer.start();

    // Signal to shutdown is deletion of runfile
    while (runFile.exists()) {
      try {
        Thread.sleep(RUNFILE_CHECK_INTERVAL_MS);
      } catch (InterruptedException e) {
        LOGGER.error(e.getMessage(), e);
        break;
      }
    }

    metricsProducer.shutdown();

    LOGGER.info("Metrics Collector process shutdown successfully");
  }

  private static AdminClient createKafkaAdminClient(String kafkaBroker) {
    Properties adminProps = new Properties();
    adminProps.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBroker);
    adminProps.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, REQUEST_TIMEOUT_MS);

    return KafkaAdminClient.create(adminProps);
  }

  private static void createTopic(AdminClient adminClient, String topic)
    throws TimeoutException, InterruptedException, ExecutionException {

    NewTopic metricsTopic = new NewTopic(topic, 1, (short) 1);

    Map<String, String> configs = metricsTopic.configs() == null ? new HashMap<>() : metricsTopic.configs();
    configs.put(TopicConfig.RETENTION_MS_CONFIG, Long.toString(LOG_RETENTION_HOURS * 60 * 60 * 1000));
    metricsTopic.configs(configs);

    CreateTopicsResult result = adminClient.createTopics(Collections.singletonList(metricsTopic));

    result.all().get();
  }
}
