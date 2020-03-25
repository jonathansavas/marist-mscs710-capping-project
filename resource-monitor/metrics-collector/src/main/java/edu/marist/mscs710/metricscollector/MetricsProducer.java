package edu.marist.mscs710.metricscollector;

public interface MetricsProducer {

  boolean start();

  boolean pause(int seconds);

  boolean wakeup();

  boolean setFrequency(int seconds);

  int getFrequency();

  boolean isStarted();

  long getNextProduceTime();

  void shutdown();
}
