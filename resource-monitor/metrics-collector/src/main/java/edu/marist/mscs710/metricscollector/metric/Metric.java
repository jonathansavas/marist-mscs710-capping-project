package edu.marist.mscs710.metricscollector.metric;

import java.util.Map;

public class Metric {
  private MetricType metricType;
  private Map<String, Object> metricData;

  public Metric(MetricType metricType, Map<String, Object> metricData) {
    this.metricType = metricType;
    this.metricData = metricData;
  }

  public MetricType getMetricType() {
    return metricType;
  }

  public Map<String, Object> getMetricData() {
    return metricData;
  }
}
