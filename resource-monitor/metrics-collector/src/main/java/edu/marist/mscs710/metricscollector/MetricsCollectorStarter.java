package edu.marist.mscs710.metricscollector;

import edu.marist.mscs710.metricscollector.producer.OSMetricsProducer;
import edu.marist.mscs710.metricscollector.utils.LoggerUtils;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.errors.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
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

  public static void main(String[] args) {
    Properties appProps = new Properties();

    try {
      appProps.load(new FileInputStream(getFileFromClasspath(PROPERTIES_FILE)));
    } catch (IOException | URISyntaxException ex) {
      LOGGER.error(LoggerUtils.getExceptionMessage(ex));
    }

    // Command-line properties should take precedence over properties file.
    appProps.putAll(System.getProperties());

    String kafkaBroker = appProps.getProperty("kafkabroker", DEFAULT_BROKER);
    String topic = appProps.getProperty("metricstopic", DEFAULT_TOPIC);

    try (AdminClient kafkaAdminClient = createKafkaAdminClient(kafkaBroker);) {
      // Test broker connection and create topic if necessary
      if (!kafkaAdminClient.listTopics().names().get().contains(topic)) {
        createTopic(kafkaAdminClient, topic);
      }
    } catch (TimeoutException | InterruptedException | ExecutionException e) {
      LOGGER.error(LoggerUtils.getExceptionMessage(e));
      return;
    }

    File runFile = new File(RUNFILE);

    try {
      runFile.createNewFile();
    } catch (IOException ex) {
      LOGGER.error(LoggerUtils.getExceptionMessage(ex));
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
        LOGGER.error(LoggerUtils.getExceptionMessage(e));
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
    CreateTopicsResult result = adminClient.createTopics(
      Collections.singletonList(
        new NewTopic(topic, 1, (short) 1)));

    result.all().get();
  }

  private static File getFileFromClasspath(String name) throws URISyntaxException {
    return new File(ClassLoader.getSystemClassLoader().getResource(name).toURI());
  }
}
