package edu.marist.mscs710.persistenceapi;

import edu.marist.mscs710.metricscollector.kafka.KafkaConfig;
import edu.marist.mscs710.metricscollector.Metric;
import edu.marist.mscs710.metricscollector.metric.NullMetric;
import edu.marist.mscs710.metricscollector.utils.LoggerUtils;
import edu.marist.mscs710.persistenceapi.db.SQLiteMetricsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Properties;

/**
 * Starts the metrics persistence process. This class listens to a topic
 * on the configured Kafka broker for <tt>Metric</tt> data and is responsible
 * for storing the data in a database.
 */
public class MetricsPersistenceStarter {
  private static final Logger LOGGER = LoggerFactory.getLogger(MetricsPersistenceStarter.class);

  private static final int RUNFILE_CHECK_INTERVAL_MS = 1000 * 2;
  private static final int IDLE_BETWEEN_POLLS = 1000 * 2;
  private static final String PROPERTIES_FILE = "application.properties";
  private static final String DEFAULT_TOPIC = "resource-monitor-metrics";
  private static final String DEFAULT_BROKER = "localhost:9092";
  private static final String DEFAULT_DB_DIR = "./db";
  private static final String CONSUMER_GROUP = "PERSISTENCE_SERVICE";
  private static final String RUNFILE = "./runfile.tmp";

  private static MetricsPersistenceService metricsPersistenceService;

  public static void main(String[] args) {
    Properties appProps = new Properties();

    try {
      appProps.load(new FileInputStream(getFileFromClasspath(PROPERTIES_FILE)));
    } catch (IOException | URISyntaxException ex) {
      LOGGER.error(LoggerUtils.getExceptionMessage(ex));
    }

    // Command-line properties should take precedence over properties file.
    appProps.putAll(System.getProperties());

    File runFile = new File(RUNFILE);

    try {
      runFile.createNewFile();
    } catch (IOException ex) {
      LOGGER.error(LoggerUtils.getExceptionMessage(ex));
      return;
    }

    runFile.deleteOnExit();

    String dbDirectory = appProps.getProperty("dbdirectory", DEFAULT_DB_DIR);
    String kafkaBroker = appProps.getProperty("kafkabroker", DEFAULT_BROKER);
    String topic = appProps.getProperty("metricstopic", DEFAULT_TOPIC);

    try {
      metricsPersistenceService = new SQLiteMetricsImpl(
        dbDirectory + "/metrics.db",
        dbDirectory + "/db_schema.sql");
    } catch (SQLException | IOException e) {
      LOGGER.error(LoggerUtils.getExceptionMessage(e));
      return;
    }

    KafkaMessageListenerContainer<String, Metric> listener = KafkaConfig.createListener(
      Collections.singletonList(kafkaBroker), CONSUMER_GROUP,
      KafkaConfig.OffsetResetPolicy.EARLIEST, topic, IDLE_BETWEEN_POLLS,
      // Listener executes this function on each message
      (consumerRecord, ack) -> {
        Metric metric = consumerRecord.value();
        if (!(metric instanceof NullMetric)) {
          metricsPersistenceService.persistMetric(metric);
        }
        ack.acknowledge();
      }
    );

    listener.start();

    LOGGER.info("Listening to topic '{}' on kafka broker at '{}' ", topic, kafkaBroker);

    while (runFile.exists()) {
      try {
        Thread.sleep(RUNFILE_CHECK_INTERVAL_MS);
      } catch (InterruptedException ex) {
        LOGGER.error(LoggerUtils.getExceptionMessage(ex));
        break;
      }
    }

    listener.stop();

    LOGGER.info("Metrics Persistence process shutdown successfully");
  }

  private static File getFileFromClasspath(String name) throws URISyntaxException {
    return new File(ClassLoader.getSystemClassLoader().getResource(name).toURI());
  }
}
