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
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class MetricsCollectorStarter {
  private static final Logger LOGGER = LoggerFactory.getLogger(MetricsCollectorStarter.class);

  private static final String PROPERTIES_FILE = "application.properties";
  private static final int REQUEST_TIMEOUT_MS = 1000 * 30;
  private static final int RUNFILE_CHECK_INTERVAL_MS = 1000 * 2;
  private static final String TOPIC = "metrics";

  private static Properties appProps = new Properties();

  public static void main(String[] args) throws IOException {
    try {
      appProps.load(new FileInputStream(PROPERTIES_FILE));
    } catch (IOException ex) {
      LOGGER.error(LoggerUtils.getExceptionMessage(ex));
    }

    // Command-line properties should take precedence over properties file.
    appProps.putAll(System.getProperties());

    String kafkaBroker = appProps.getProperty("kafkabroker", "localhost:9092");

    Properties adminProps = new Properties();
    adminProps.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBroker);
    adminProps.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, REQUEST_TIMEOUT_MS);

    AdminClient kafkaAdminClient = KafkaAdminClient.create(adminProps);

    try {
      // Test broker connection and create topic if necessary
      if (!kafkaAdminClient.listTopics().names().get().contains(TOPIC)) {
        createTopic(kafkaAdminClient);
      }
    } catch (TimeoutException | InterruptedException | ExecutionException e) {
      LOGGER.error(LoggerUtils.getExceptionMessage(e));
      return;
    }

    kafkaAdminClient.close();

    String runFilePath = appProps.getProperty("runfile", "./metrics-collector-runfile.tmp");
    File runFile = new File(runFilePath);

    runFile.createNewFile();
    runFile.deleteOnExit();

    MetricsProducer metricsProducer = new OSMetricsProducer(Collections.singletonList(kafkaBroker), TOPIC);
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

  private static void createTopic(AdminClient adminClient)
    throws TimeoutException, InterruptedException, ExecutionException {
    CreateTopicsResult result = adminClient.createTopics(
      Collections.singletonList(
        new NewTopic(MetricsCollectorStarter.TOPIC, 1, (short) 1)));

    result.all().get();
  }
}
