package edu.marist.mscs710.metricscollector.metric;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class Metric {
  private MetricType metricType;

  private Map<String, Object> metricData;

  @JsonCreator
  public Metric(@JsonProperty(value = "metricType") MetricType metricType,
                @JsonProperty(value = "metricData") Map<String, Object> metricData) {
    this.metricType = metricType;
    this.metricData = metricData;
  }

  public MetricType getMetricType() {
    return metricType;
  }

  public Map<String, Object> getMetricData() {
    return metricData;
  }

  @Override
  public String toString() {
    return "Metric{" +
      "metricType=" + metricType +
      ", metricData=" + metricData +
      '}';
  }
}
