package edu.marist.mscs710.metricscollector.data;

public abstract class MetricData {
  protected long deltaMillis;

  public long getDeltaMillis() {
    return deltaMillis;
  }
}
