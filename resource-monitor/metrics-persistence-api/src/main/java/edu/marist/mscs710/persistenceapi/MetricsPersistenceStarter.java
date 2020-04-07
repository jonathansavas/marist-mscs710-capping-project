package edu.marist.mscs710.persistenceapi;

import edu.marist.mscs710.metricscollector.kafka.KafkaConfig;
import edu.marist.mscs710.metricscollector.metric.Metric;
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
  private static final String TOPIC = "metrics";
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

    String dbDirectory = appProps.getProperty("dbdirectory", "./db");
    File runFile = new File(RUNFILE);

    try {
      runFile.createNewFile();
    } catch (IOException ex) {
      LOGGER.error(LoggerUtils.getExceptionMessage(ex));
      return;
    }

    runFile.deleteOnExit();

    try {
      metricsPersistenceService = new SQLiteMetricsImpl(
        dbDirectory + "/metrics.db",
        dbDirectory + "/db_schema.sql");
    } catch (SQLException | IOException e) {
      LOGGER.error(LoggerUtils.getExceptionMessage(e));
      return;
    }

    String kafkaBroker = appProps.getProperty("kafkabroker", "localhost:9092");

    KafkaMessageListenerContainer<String, Metric> listener = KafkaConfig.createListener(
      Collections.singletonList(kafkaBroker), CONSUMER_GROUP,
      KafkaConfig.OffsetResetPolicy.EARLIEST, TOPIC, IDLE_BETWEEN_POLLS,
      (metric, ack) -> {
        metricsPersistenceService.persistMetric(metric.value());
        ack.acknowledge();
      }
    );

    listener.start();

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
