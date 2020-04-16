package edu.marist.mscs710.metricscollector.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.marist.mscs710.metricscollector.metric.Fields;
import edu.marist.mscs710.metricscollector.metric.Metric;

import java.util.List;

public class CpuCoreData extends MetricData {

  @JsonProperty(Fields.CPU_CORE_CORE_ID)
  private int coreId;

  @JsonProperty(Fields.CPU_CORE_CORE_UTILIZATION)
  private double coreUtilization;

  public CpuCoreData(int coreId, double coreUtilization, long deltaMillis, long epochMillisTime) {
    this.coreId = coreId;
    this.coreUtilization = coreUtilization;
    this.deltaMillis = deltaMillis;
    this.epochMillisTime = epochMillisTime;
  }

  public int getCoreId() {
    return coreId;
  }

  public double getCoreUtilization() {
    return coreUtilization;
  }

  @Override
  public String toString() {
    return "CpuCoreData{" +
      "coreId=" + coreId +
      ", coreUtilization=" + coreUtilization +
      ", deltaMillis=" + deltaMillis +
      ", epochMillisTime=" + epochMillisTime +
      '}';
  }

  @Override
  public List<? extends Metric> toMetricRecords() {
    return null;
  }
}
