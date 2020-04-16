package edu.marist.mscs710.metricscollector.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.marist.mscs710.metricscollector.metric.Fields;
import edu.marist.mscs710.metricscollector.metric.Metric;

import java.util.List;

/**
 * Holds a snapshot of Memory data.
 */
public class MemoryData extends MetricData {

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
  public List<Metric> toMetricRecords() {
    return null;
  }
}
