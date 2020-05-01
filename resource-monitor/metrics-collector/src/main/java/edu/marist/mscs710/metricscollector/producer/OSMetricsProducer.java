package edu.marist.mscs710.metricscollector.producer;

import edu.marist.mscs710.metricscollector.MetricSource;
import edu.marist.mscs710.metricscollector.MetricsProducer;
import edu.marist.mscs710.metricscollector.data.MetricData;
import edu.marist.mscs710.metricscollector.kafka.MetricSender;
import edu.marist.mscs710.metricscollector.system.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * A <tt>MetricsProducer</tt> for operating system metrics. This process
 * runs indefinitely, sending data to a configured Kafka cluster in
 * (approximately) regular intervals.
 */
public class OSMetricsProducer implements MetricsProducer {
  private static final Logger LOGGER = LoggerFactory.getLogger(OSMetricsProducer.class);

  private static final boolean FROM_NOW = true;
  private static final boolean PLUS_NEXT = false;
  private static final int MINIMUM_FREQUENCY = 5;
  private static final int DEFAULT_FREQUENCY = MINIMUM_FREQUENCY;

  private AtomicInteger frequency;
  private AtomicLong nextProduceTime;
  private AtomicBoolean collecting;
  private AtomicBoolean started;

  private List<MetricSource> metricSources;
  private MetricSender metricSender;
  private String topic;

  private SystemConstants systemConstants;
  private Thread collectorThread;


  /**
   * Constructs a new <tt>OSMetricsProducer</tt> configured to a Kafka cluster.
   *
   * @param frequency    number of seconds between metric collection, minimum five seconds
   * @param kafkaBrokers list of Kafka brokers in form host:port
   * @param topic        topic to which to send metric data
   */
  public OSMetricsProducer(int frequency, List<String> kafkaBrokers, String topic) {
    this.frequency = new AtomicInteger(Math.max(frequency, MINIMUM_FREQUENCY));
    this.collecting = new AtomicBoolean(false);
    this.nextProduceTime = new AtomicLong(-1L);
    this.started = new AtomicBoolean(false);
    this.metricSources = new ArrayList<>();
    this.metricSender = new MetricSender(kafkaBrokers);
    this.topic = topic;
    this.systemConstants = new SystemConstants();

    LOGGER.info("Initialized Metrics Producer with frequency {} seconds, waiting for start command", frequency);
  }

  /**
   * Constructs a new <tt>OSMetricsProducer</tt> configured to a Kafka cluster
   * with default frequency of five seconds.
   *
   * @param kafkaBrokers list of Kafka brokers in form host:port
   * @param topic        topic to which to send metric data
   */
  public OSMetricsProducer(List<String> kafkaBrokers, String topic) {
    this(DEFAULT_FREQUENCY, kafkaBrokers, topic);
  }

  @Override
  public boolean start() {
    if (started.get()) {
      LOGGER.error("Attempted to start Metrics Producer that is already running");
      return false;
    } else {
      LOGGER.info("Starting Metrics Producer process");
      addAllMetricSources();
      setNextProduceTime(frequency.get(), FROM_NOW);
      this.run();
      started.set(true);
      LOGGER.info("Metrics Producer successfully started");
      return true;
    }
  }

  @Override
  public boolean pause(int seconds) {
    if (!started.get()) {
      LOGGER.error("Pause command failed, Metrics Producer is not yet started");
      return false;
    } else if (seconds == 0) {
      LOGGER.error("Pause command failed, cannot pause for {} seconds", seconds);
      return false;
    } else if (nextProduceTime.get() < 0) {
      LOGGER.error("Pause command failed, Metrics Producer is paused indefinitely");
      return false;
    } else if (seconds > 0) {
      setNextProduceTime(seconds, PLUS_NEXT);
      LOGGER.info("Pausing metrics collection for {} seconds", seconds);
      return true;
    } else {
      pauseIndefinitely();
      LOGGER.info("Pausing metrics collection until wakeup command is received");
      return true;
    }
  }

  @Override
  public boolean wakeup() {
    if (!started.get()) {
      LOGGER.error("Wakeup command failed, Metrics Producer not yet started");
      return false;
    } else if (frequency.get() >= 0) {
      LOGGER.error("Wakeup command failed, Metrics Producer is running");
      return false;
    } else {
      setNextProduceTime(frequency.get(), FROM_NOW);
      LOGGER.info("Successful wakeup");
      return true;
    }
  }

  @Override
  public boolean setFrequency(int seconds) {
    if (seconds < MINIMUM_FREQUENCY) {
      LOGGER.error("Attempt to set frequency below minimum frequency of {} seconds", MINIMUM_FREQUENCY);
      return false;
    } else {
      frequency.set(seconds);
      LOGGER.info("Updated frequency to {} seconds", seconds);
      return true;
    }
  }

  @Override
  public boolean shutdown() {
    if (!started.get()) {
      LOGGER.error("Shutdown command failed, Metrics Producer not yet started");
      return false;
    } else {
      collecting.set(false);
      try {
        collectorThread.join();
      } catch (InterruptedException e) {
        LOGGER.error(e.getMessage(), e);
      }
      metricSender.close();
      return true;
    }
  }

  @Override
  public int getFrequency() {
    return frequency.get();
  }

  @Override
  public long getNextProduceTime() {
    return nextProduceTime.get();
  }

  @Override
  public boolean isStarted() {
    return started.get();
  }

  private void run() {
    collecting.set(true);
    sendSystemConstants();
    collectorThread = new Thread(this::produceMetrics);
    collectorThread.start();
  }

  private void produceMetrics() {
    LOGGER.info("Started metrics production thread");

    while (collecting.get()) {
      long sleepTime = getSleepTime();
      while (collecting.get() && sleepTime > 0) {
        try {
          Thread.sleep(sleepTime);
          sleepTime = getSleepTime();
        } catch (InterruptedException e) {
          LOGGER.error(e.getMessage(), e);
          return;
        }
      }

      setNextProduceTime(frequency.get(), FROM_NOW);

      List<MetricData> metricDataList = metricSources.stream()
        .map(MetricSource::getMetricData)
        .flatMap(List::stream)
        .collect(Collectors.toList());

      for (MetricData metricData : metricDataList) {
        metricSender.send(topic, metricData);
      }

      metricSender.flush();
    }

    LOGGER.info("Shutting down metrics production thread");
  }

  private long getSleepTime() {
    long timeRemaining = nextProduceTime.get() - Instant.now().toEpochMilli();

    // Control sleep time while waiting for next produce time
    // Sleep a shorter length of time the closer we get
    // These numbers are somewhat arbitrary
    if (timeRemaining < 15)
      return 0;
    else if (timeRemaining < 100)
      return timeRemaining - 10;
    else if (timeRemaining < 500)
      return timeRemaining - 50;
    else
      return 1000;
  }

  private void addAllMetricSources() {
    if (!started.get()) {
      LOGGER.info("Collection CPU, Memory, Network, System, and Processes metrics");
      metricSources.add(new Cpu(true));
      metricSources.add(new Memory());
      metricSources.add(new Network());
      metricSources.add(new SystemMetrics());
      metricSources.add(new Processes());
    }
  }

  private void setNextProduceTime(int seconds, boolean fromNow) {
    if (fromNow)
      nextProduceTime.set(Instant.now().plusSeconds(seconds).toEpochMilli());
    else
      nextProduceTime.set(Instant.ofEpochMilli(nextProduceTime.get()).plusSeconds(seconds).toEpochMilli());
  }

  private void pauseIndefinitely() {
    nextProduceTime.set(-1L);
  }

  private void sendSystemConstants() {
    metricSender.send(topic, systemConstants);
  }
}
