package edu.marist.mscs710.metricscollector.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.marist.mscs710.metricscollector.metric.Fields;

import java.util.List;

import static edu.marist.mscs710.metricscollector.utils.DataUtils.weightedAverage;

/**
 * Holds a snapshot of Memory data.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MemoryData extends MetricData {
  public static final String SQL_INSERT_PREFIX = "INSERT INTO " +
    Fields.METRIC_TYPE_MEMORY + " (" +
    Fields.MEMORY_DATETIME + ',' +
    Fields.MEMORY_DELTA_MILLIS + ',' +
    Fields.MEMORY_PAGE_FAULTS + ',' +
    Fields.MEMORY_UTILIZATION + ") VALUES ";

  @JsonProperty(Fields.MEMORY_UTILIZATION)
  private double memoryUtilization;

  @JsonProperty(Fields.MEMORY_PAGE_FAULTS)
  private double pageFaults;

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

  /**
   * Constructs a new <tt>MemoryData</tt> with the supplied metrics.
   *
   * @param memoryUtilization memory utilization from 0.0-1.0
   * @param pageFaults        number of page faults per second during this snapshot
   * @param deltaMillis       time covered by this snapshot
   * @param epochMillisTime   epoch milli timestamp of this snapshot
   */
  @JsonCreator
  public MemoryData(@JsonProperty(Fields.MEMORY_UTILIZATION) double memoryUtilization,
                    @JsonProperty(Fields.MEMORY_PAGE_FAULTS) double pageFaults,
                    @JsonProperty(Fields.DELTA_MILLIS) long deltaMillis,
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
   * Gets the number of page faults per second during this snapshot
   *
   * @return page faults rate
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MemoryData that = (MemoryData) o;
    return Double.compare(that.memoryUtilization, memoryUtilization) == 0 &&
      Double.compare(that.pageFaults, pageFaults) == 0&&
      deltaMillis == that.deltaMillis &&
      epochMillisTime == that.epochMillisTime;
  }

  @Override
  public String toSqlInsertString() {
    return SQL_INSERT_PREFIX + '(' +
      epochMillisTime + ',' +
      deltaMillis + ',' +
      pageFaults + ',' +
      memoryUtilization + ')' + ';';
  }

  /**
   * Combines a list of <tt>MemoryData</tt> into a single instance. This method
   * takes a weighted average of all fields based on <tt>deltaMillis</tt>.
   *
   * @param metrics list of memory metrics
   * @return an aggregate <tt>MemoryData</tt> instance
   */
  public static MemoryData combine(List<MemoryData> metrics) {
    double datetime = 0;
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

    return new MemoryData(utilization, pageFaults, totalMillis, (long) datetime);
  }
}
