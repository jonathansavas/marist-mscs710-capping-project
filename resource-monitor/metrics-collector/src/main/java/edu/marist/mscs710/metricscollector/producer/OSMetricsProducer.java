package edu.marist.mscs710.metricscollector.producer;

import edu.marist.mscs710.metricscollector.MetricSource;
import edu.marist.mscs710.metricscollector.MetricsProducer;
import edu.marist.mscs710.metricscollector.data.MetricData;
import edu.marist.mscs710.metricscollector.metric.Metric;
import edu.marist.mscs710.metricscollector.metric.MetricType;
import edu.marist.mscs710.metricscollector.system.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class OSMetricsProducer implements MetricsProducer {
  private static final boolean FROM_NOW = true;
  private static final boolean PLUS_NEXT = false;
  private static final int MINIMUM_FREQUENCY = 5;
  private static final int DEFAULT_FREQUENCY = MINIMUM_FREQUENCY;

  private AtomicInteger frequency;
  private AtomicLong nextProduceTime;
  private AtomicBoolean running;
  private AtomicBoolean started;

  private List<MetricSource> metricSources;


  public OSMetricsProducer(int frequency) {
    this.frequency = new AtomicInteger(Math.max(frequency, MINIMUM_FREQUENCY));
    this.running = new AtomicBoolean(false);
    this.nextProduceTime = new AtomicLong(-1L);
    this.started = new AtomicBoolean(false);
    this.metricSources = new ArrayList<>();
  }

  public OSMetricsProducer() {
    this(DEFAULT_FREQUENCY);
  }

  @Override
  public boolean start() {
    if (started.get()) {
      return false;
    } else {
      addAllMetricSources();
      setNextProduceTime(frequency.get(), FROM_NOW);
      this.run();
      started.set(true);
      return true;
    }
  }

  @Override
  public boolean pause(int seconds) {
    if (!started.get() || seconds == 0 || nextProduceTime.get() < 0) return false;

    if (seconds > 0)
      setNextProduceTime(seconds, PLUS_NEXT);
    else
      pauseIndefinitely();
    return true;
  }

  @Override
  public boolean wakeup() {
    if (!started.get() || frequency.get() >= 0) return false;

    setNextProduceTime(frequency.get(), FROM_NOW);
    return true;
  }

  @Override
  public boolean setFrequency(int seconds) {
    if (seconds < MINIMUM_FREQUENCY) {
      return false;
    } else {
      frequency.set(seconds);
      return true;
    }
  }

  @Override
  public void shutdown() {
    running.set(false);
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
    running.set(true);
    new Thread(this::produceMetrics).start();
  }

  private void produceMetrics() {
    while (running.get()) {
      long sleepTime;
      while ((sleepTime = getSleepTime()) > 0) {
        try {
          Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
          e.printStackTrace(); // TODO: what should we do here?
        }
      }

      setNextProduceTime(frequency.get(), FROM_NOW);

      List<MetricData> metricDataList = metricSources.stream()
        .map(MetricSource::getMetricData)
        .flatMap(List::stream)
        .collect(Collectors.toList());

      for (MetricData metricData : metricDataList) {
        for (Metric metric : metricData.toMetricRecords())
          sendToKafka(metric);
      }

      System.out.println();
    }
  }

  private void sendToKafka(Metric metric) {
    if (metric.getMetricType() != MetricType.PROCESSES) {
      System.out.println(Instant.now().toEpochMilli());
      System.out.println(metric.toString());
      System.out.println();
    }
  }

  private long getSleepTime() {
    long timeRemaining = nextProduceTime.get() - Instant.now().toEpochMilli();

    if (timeRemaining < 15)
      return 0;
    else if (timeRemaining < 100)
      return timeRemaining - 10;
    else if (timeRemaining < 500)
      return timeRemaining - 50;
    else
      return timeRemaining - 100;
  }

  private void addAllMetricSources() {
    if (!started.get()) {
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
}
