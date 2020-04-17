package edu.marist.mscs710.metricscollector.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.marist.mscs710.metricscollector.metric.Fields;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static edu.marist.mscs710.metricscollector.utils.DataUtils.weightedAverage;

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

  public static List<CpuCoreData> combine(List<CpuCoreData> metrics) {
    Map<Integer, List<CpuCoreData>> metricsById = metrics.stream()
      .collect(Collectors.groupingBy(CpuCoreData::getCoreId, Collectors.toList()));

    List<CpuCoreData> combinedMetrics = new ArrayList<>();

    for (Map.Entry<Integer, List<CpuCoreData>> entry : metricsById.entrySet()) {
      int coreId = entry.getKey();
      long datetime = 0;
      long totalMillis = 0;
      double coreUtilization = 0.0;

      for (CpuCoreData data : entry.getValue()) {
        long deltaMillis = data.getDeltaMillis();
        datetime = weightedAverage(datetime, totalMillis, data.getEpochMillisTime(), deltaMillis);
        coreUtilization = weightedAverage(coreUtilization, totalMillis, data.getCoreUtilization(), deltaMillis);
        totalMillis += deltaMillis;
      }

      combinedMetrics.add(new CpuCoreData(coreId, coreUtilization, totalMillis, datetime));
    }

    return combinedMetrics;
  }
}
