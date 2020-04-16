package edu.marist.mscs710.metricscollector.metric;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds a snapshot of generic metric data.
 */
public class Metric {
  private MetricType metricType;

  private Map<String, Object> metricData;

  /**
   * Constructs a new <tt>Metric</tt> with the supplied metrics.
   *
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
   *
   * @return metric type
   */
  public MetricType getMetricType() {
    return metricType;
  }

  /**
   * Gets the metric data.
   *
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

  /*public static Metric combine(List<Metric> metrics) {
    if (metrics == null || metrics.isEmpty())
      return new Metric(MetricType.NULL, new HashMap<>());


  }*/

  /*private static Metric combineCpuMetrics(List<Metric> metrics) {
    long datetime = 0;
    long totalMillis = 0;
    double temperature = 0;
    double utilization = 0;

    for (Metric metric : metrics) {
      Map<String, Object> metricData = metric.getMetricData();
      long deltaMillis = (long) metricData.get(Fields.Cpu.DELTA_MILLIS.toString());
      datetime = weightedAverage(datetime, totalMillis, (long) metricData.get(Fields.Cpu.DATETIME.toString()), deltaMillis);
      temperature = weightedAverage(temperature, totalMillis, (double) metricData.get(Fields.Cpu.TEMPERATURE.toString()), deltaMillis);
      utilization = weightedAverage(utilization, totalMillis, (double) metricData.get(Fields.Cpu.UTILIZATION.toString()), deltaMillis);
      totalMillis += deltaMillis;
    }

    return new Metric(metrics.get(0).getMetricType(), )
  }*/

  private static long weightedAverage(long agg, long wAgg, long val, long wVal) {
    return (long) (agg + (val - agg) * ((double) wVal / (wVal + wAgg)));
  }

  private static double weightedAverage(double agg, double wAgg, double val, double wVal) {
    return agg + (val - agg) * ( wVal / (wVal + wAgg));
  }
}
