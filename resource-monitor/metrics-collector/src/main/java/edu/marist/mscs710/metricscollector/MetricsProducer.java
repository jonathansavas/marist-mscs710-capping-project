package edu.marist.mscs710.metricscollector;

/**
 * Provides a common interface to interact with producer of system metrics.
 */
public interface MetricsProducer {

  /**
   * Start collection of metrics.
   * @return true if the operation is successful, false otherwise
   */
  boolean start();

  /**
   * Pause collection of metrics for the specified number of seconds. A negative
   * value will cause metric collection to pause indefinitely. This value should
   * not be zero.
   * @param seconds number of seconds to pause
   * @return true if the operation is successful, false otherwise
   */
  boolean pause(int seconds);

  /**
   * Restarts metric collection if the producer is paused indefinitely.
   * @return true if the operation is successful, false otherwise
   */
  boolean wakeup();

  /**
   * Sets the frequency of metric collection.
   * @param seconds number of seconds between metric collection operations
   * @return true if the operation is successful, false otherwise
   */
  boolean setFrequency(int seconds);

  /**
   * Gets the current frequency.
   * @return seconds between metric collection operations
   */
  int getFrequency();

  /**
   * Checks if the <tt>MetricsProducer</tt> has started.
   * @return true if the <tt>MetricsProducer</tt> has started, false otherwise
   */
  boolean isStarted();

  /**
   * Gets the epoch milli timestamp of the next scheduled metric collection operation.
   * @return
   */
  long getNextProduceTime();

  /**
   * Stops the <tt>MetricsProducer</tt>
   */
  void shutdown();
}
