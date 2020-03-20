package edu.marist.mscs710.metricscollector.data;

public abstract class MetricData {
  protected long deltaMillis;
  protected long epochMillisTime;

  public long getDeltaMillis() {
    return deltaMillis;
  }

  public long getEpochMillisTime() {
    return epochMillisTime;
  }
}
