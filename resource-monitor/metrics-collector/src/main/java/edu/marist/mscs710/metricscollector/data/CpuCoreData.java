package edu.marist.mscs710.metricscollector.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.marist.mscs710.metricscollector.metric.Fields;

public class CpuCoreData extends MetricData {
  public static final String SQL_INSERT_PREFIX = "INSERT INTO " +
    Fields.METRIC_TYPE_CPU_CORE + " (" +
    Fields.CPU_CORE_DATETIME + ',' +
    Fields.CPU_CORE_DELTA_MILLIS + ',' +
    Fields.CPU_CORE_CORE_UTILIZATION + ',' +
    Fields.CPU_CORE_CORE_ID + ") VALUES ";

  @JsonProperty(Fields.CPU_CORE_CORE_ID)
  private int coreId;

  @JsonProperty(Fields.CPU_CORE_CORE_UTILIZATION)
  private double coreUtilization;

  @JsonCreator
  public CpuCoreData(@JsonProperty(Fields.CPU_CORE_CORE_ID) int coreId,
                     @JsonProperty(Fields.CPU_CORE_CORE_UTILIZATION) double coreUtilization,
                     @JsonProperty(Fields.DELTA_MILLIS) long deltaMillis,
                     @JsonProperty(Fields.DATETIME) long epochMillisTime) {
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
  public String toSqlInsertString() {
    return SQL_INSERT_PREFIX + '(' +
      epochMillisTime + ',' +
      deltaMillis + ',' +
      coreUtilization + ',' +
      coreId + ')' + ';';
  }
}
