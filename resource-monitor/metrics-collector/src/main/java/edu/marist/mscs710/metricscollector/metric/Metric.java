package edu.marist.mscs710.metricscollector.metric;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Holds a snapshot of generic metric data.
 */
public class Metric {
  private MetricType metricType;

  private Map<String, Object> metricData;

  /**
   * Constructs a new <tt>Metric</tt> with the supplied metrics.
   * @param metricType type of the metric
   * @param metricData map of metric data
   */
  @JsonCreator
  public Metric(@JsonProperty(value = "metricType") MetricType metricType,
                @JsonProperty(value = "metricData") Map<String, Object> metricData) {
    this.metricType = metricType;
    this.metricData = metricData;
  }

  /**
   * Gets the metric type.
   * @return metric type
   */
  public MetricType getMetricType() {
    return metricType;
  }

  /**
   * Gets the metric data.
   * @return metric data
   */
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
