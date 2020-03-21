package edu.marist.mscs710.metricscollector.data;

import edu.marist.mscs710.metricscollector.MetricRecord;

public abstract class MetricData implements MetricRecord {
  protected long deltaMillis;
  protected long epochMillisTime;

  public long getDeltaMillis() {
    return deltaMillis;
  }

  public long getEpochMillisTime() {
    return epochMillisTime;
  }
}
