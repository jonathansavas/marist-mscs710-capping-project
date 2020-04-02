package edu.marist.mscs710.metricscollector.data;

import edu.marist.mscs710.metricscollector.metric.Fields;
import edu.marist.mscs710.metricscollector.metric.Metric;
import edu.marist.mscs710.metricscollector.metric.MetricType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds a snapshot of Memory data.
 */
public class MemoryData extends MetricData {
  private double memoryUtilization; // Pct memory in use
  private long pageFaults; // during time span delta millis

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
  public long getPageFaults() {
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

  private Map<String, Object> getMemoryMap() {
    return new HashMap<String, Object>() {
      {
        put(Fields.Memory.DATETIME.toString(), epochMillisTime);
        put(Fields.Memory.DELTA_MILLIS.toString(), deltaMillis);
        put(Fields.Memory.UTILIZATION.toString(), memoryUtilization);
        put(Fields.Memory.PAGE_FAULTS.toString(), ((double) pageFaults) / deltaMillis * 1000.0);
      }
    };
  }

  @Override
  public List<Metric> toMetricRecords() {
    return Collections.singletonList(new Metric(MetricType.MEMORY, getMemoryMap()));
  }
}
