package edu.marist.mscs710.metricscollector.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.marist.mscs710.metricscollector.metric.Fields;

/**
 * Holds a snapshot of CPU data. CPU usage metrics are given as percent
 * utilization from 0.0-1.0.
 */
public class CpuData extends MetricData {
  public static final String SQL_INSERT_PREFIX = "INSERT INTO " +
    Fields.METRIC_TYPE_CPU + " (" +
    Fields.CPU_DATETIME + ',' +
    Fields.CPU_DELTA_MILLIS + ',' +
    Fields.CPU_UTILIZATION + ',' +
    Fields.CPU_TEMPERATURE + ") VALUES ";

  @JsonProperty(Fields.CPU_UTILIZATION)
  private double utilization; // Total cpu usage during previous delta millis

  @JsonProperty(Fields.CPU_TEMPERATURE)
  private double temperature; // degrees C, 0.0 if not available, need elevated permissions in Windows

  /**
   * Constructs a new <tt>CpuData</tt> with the supplied metrics.
   *
   * @param utilization   overall CPU usage during this snapshot
   * @param temperature         cpu temperature in degrees Celsius
   * @param deltaMillis     time covered by this snapshot
   * @param epochMillisTime epoch milli timestamp of this snapshot
   */
  @JsonCreator
  public CpuData(@JsonProperty(Fields.CPU_UTILIZATION) double utilization,
                 @JsonProperty(Fields.CPU_TEMPERATURE) double temperature,
                 @JsonProperty(Fields.DELTA_MILLIS)long deltaMillis,
                 @JsonProperty(Fields.DATETIME) long epochMillisTime) {
    this.utilization = utilization;
    this.temperature = temperature;
    this.deltaMillis = deltaMillis;
    this.epochMillisTime = epochMillisTime;
  }

  /**
   * Gets the overall CPU usage.
   *
   * @return overall CPU usage
   */
  public double getUtilization() {
    return utilization;
  }

  /**
   * Gets the CPU temperature.
   *
   * @return CPU temperature in degrees Celsius, at epochMillisTime,
   * or 0.0 if unavailable
   */
  public double getTemperature() {
    return temperature;
  }

  @Override
  public String toString() {
    return "CpuData{" +
      "totalCpuUsage=" + utilization +
      ", cpuTemp=" + temperature +
      ", deltaMillis=" + deltaMillis +
      ", epochMillisTime=" + epochMillisTime +
      '}';
  }

  @Override
  public String toSqlInsertString() {
    return SQL_INSERT_PREFIX + '(' +
      epochMillisTime + ',' +
      deltaMillis + ',' +
      utilization + ',' +
      temperature + ')' + ';';
  }
}
