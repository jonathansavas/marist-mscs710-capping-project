package edu.marist.mscs710.metricscollector.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.marist.mscs710.metricscollector.metric.Fields;

import java.util.List;

import static edu.marist.mscs710.metricscollector.utils.DataUtils.weightedAverage;

/**
 * Holds a snapshot of Memory data.
 */
public class MemoryData extends MetricData {
  public static final String SQL_INSERT_PREFIX = "INSERT INTO " +
    Fields.METRIC_TYPE_MEMORY + " (" +
    Fields.MEMORY_DATETIME + ',' +
    Fields.MEMORY_DELTA_MILLIS + ',' +
    Fields.MEMORY_PAGE_FAULTS + ',' +
    Fields.MEMORY_UTILIZATION + ") VALUES ";

  @JsonProperty(Fields.MEMORY_UTILIZATION)
  private double memoryUtilization; // Pct memory in use

  @JsonProperty(Fields.MEMORY_PAGE_FAULTS)
  private double pageFaults; // during time span delta millis

  /**
   * Constructs a new <tt>MemoryData</tt> with the supplied metrics.
   *
   * @param memoryUtilization memory utilization from 0.0-1.0
   * @param pageFaults        number of page faults during this snapshot
   * @param deltaMillis       time covered by this snapshot
   * @param epochMillisTime   epoch milli timestamp of this snapshot
   */
  public MemoryData(double memoryUtilization, long pageFaults, long deltaMillis, long epochMillisTime) {
    this.memoryUtilization = memoryUtilization;
    this.pageFaults = ((double) pageFaults) / deltaMillis * 1000.0;
    this.deltaMillis = deltaMillis;
    this.epochMillisTime = epochMillisTime;
  }

  @JsonCreator
  public MemoryData(@JsonProperty(Fields.MEMORY_UTILIZATION) double memoryUtilization,
                    @JsonProperty(Fields.MEMORY_PAGE_FAULTS) double pageFaults,
                    @JsonProperty(Fields.DELTA_MILLIS)long deltaMillis,
                    @JsonProperty(Fields.DATETIME) long epochMillisTime) {
    this.memoryUtilization = memoryUtilization;
    this.pageFaults = pageFaults;
    this.deltaMillis = deltaMillis;
    this.epochMillisTime = epochMillisTime;
  }

  /**
   * Gets the memory utilization.
   *
   * @return overall memory utilization.
   */
  public double getMemoryUtilization() {
    return memoryUtilization;
  }

  /**
   * Gets the number of page faults during this snapshot
   *
   * @return number of page faults
   */
  public double getPageFaults() {
    return pageFaults;
  }

  @Override
  public String toString() {
    return "MemoryData{" +
      "memoryUtilization=" + memoryUtilization +
      ", pageFaults=" + pageFaults +
      ", deltaMillis=" + deltaMillis +
      ", epochMillisTime=" + epochMillisTime +
      '}';
  }

  @Override
  public String toSqlInsertString() {
    return SQL_INSERT_PREFIX + '(' +
      epochMillisTime + ',' +
      deltaMillis + ',' +
      pageFaults + ',' +
      memoryUtilization + ')' + ';';
  }

  public static MemoryData combine(List<MemoryData> metrics) {
    long datetime = 0;
    long totalMillis = 0;
    double utilization = 0.0;
    double pageFaults = 0.0;

    for (MemoryData data : metrics) {
      long deltaMillis = data.getDeltaMillis();
      datetime = weightedAverage(datetime, totalMillis, data.getEpochMillisTime(), deltaMillis);
      utilization = weightedAverage(utilization, totalMillis, data.getMemoryUtilization(), deltaMillis);
      pageFaults = weightedAverage(pageFaults, totalMillis, data.getPageFaults(), deltaMillis);
      totalMillis += deltaMillis;
    }

    return new MemoryData(utilization, pageFaults, totalMillis, datetime);
  }
}
