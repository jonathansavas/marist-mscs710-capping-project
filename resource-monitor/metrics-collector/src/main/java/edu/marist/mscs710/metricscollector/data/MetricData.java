package edu.marist.mscs710.metricscollector.data;

import edu.marist.mscs710.metricscollector.MetricRecord;

/**
 * Abstract base class for all other <tt>MetricData</tt> objects.
 */
public abstract class MetricData implements MetricRecord {
  protected long deltaMillis;
  protected long epochMillisTime;

  /**
   * Gets time in milliseconds covered by this snapshot.
   * @return number of milliseconds
   */
  public long getDeltaMillis() {
    return deltaMillis;
  }

  /**
   * Gets the time epoch milli timestamp of this snapshot.
   * @return epoch milli timestamp
   */
  public long getEpochMillisTime() {
    return epochMillisTime;
  }
}
