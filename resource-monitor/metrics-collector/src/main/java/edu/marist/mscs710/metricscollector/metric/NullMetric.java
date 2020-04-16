package edu.marist.mscs710.metricscollector.metric;

import edu.marist.mscs710.metricscollector.Metric;

public class NullMetric implements Metric {

  @Override
  public String toSqlInsertString() {
    return "";
  }
}
