package edu.marist.mscs710.metricscollector.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.marist.mscs710.metricscollector.metric.Fields;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static edu.marist.mscs710.metricscollector.utils.DataUtils.weightedAverage;

/**
 * Holds a snapshot of data for an individual CPU core.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
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

  /**
   * Constructs a new <tt>CpuCoreData</tt> with the supplied metrics.
   *
   * @param coreId          id number of the CPU core
   * @param coreUtilization core CPU usage during this snapshot
   * @param deltaMillis     time covered by this snapshot
   * @param epochMillisTime epoch milli timestamp of this snapshot
   */
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

  /**
   * Gets the core id.
   *
   * @return core id number
   */
  public int getCoreId() {
    return coreId;
  }

  /**
   * Gets the core utilization.
   *
   * @return core utilization from 0.0-1.0
   */
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CpuCoreData that = (CpuCoreData) o;
    return coreId == that.coreId &&
      Double.compare(that.coreUtilization, coreUtilization) == 0&&
      deltaMillis == that.deltaMillis &&
      epochMillisTime == that.epochMillisTime;
  }

  @Override
  public String toSqlInsertString() {
    return SQL_INSERT_PREFIX + '(' +
      epochMillisTime + ',' +
      deltaMillis + ',' +
      coreUtilization + ',' +
      coreId + ')' + ';';
  }

  /**
   * Combines a list of <tt>CpuCoreData</tt> into single instances grouped by
   * core id. This method takes a weighted average of all fields based on
   * <tt>deltaMillis</tt>.
   *
   * @param metrics list of CPU core metrics
   * @return list of aggregated <tt>CpuCoreData</tt> instances, one for each
   *         unique core id
   */
  public static List<CpuCoreData> combine(List<CpuCoreData> metrics) {
    Map<Integer, List<CpuCoreData>> metricsById = metrics.stream()
      .collect(Collectors.groupingBy(CpuCoreData::getCoreId, Collectors.toList()));

    List<CpuCoreData> combinedMetrics = new ArrayList<>();

    for (Map.Entry<Integer, List<CpuCoreData>> entry : metricsById.entrySet()) {
      int coreId = entry.getKey();
      double datetime = 0;
      long totalMillis = 0;
      double coreUtilization = 0.0;

      for (CpuCoreData data : entry.getValue()) {
        long deltaMillis = data.getDeltaMillis();
        datetime = weightedAverage(datetime, totalMillis, data.getEpochMillisTime(), deltaMillis);
        coreUtilization = weightedAverage(coreUtilization, totalMillis, data.getCoreUtilization(), deltaMillis);
        totalMillis += deltaMillis;
      }

      combinedMetrics.add(new CpuCoreData(coreId, coreUtilization, totalMillis, (long) datetime));
    }

    return combinedMetrics;
  }
}
