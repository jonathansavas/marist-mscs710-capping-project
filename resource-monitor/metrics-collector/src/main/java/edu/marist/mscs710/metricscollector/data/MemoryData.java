package edu.marist.mscs710.metricscollector.data;

import edu.marist.mscs710.metricscollector.metric.Fields;
import edu.marist.mscs710.metricscollector.metric.Metric;
import edu.marist.mscs710.metricscollector.metric.MetricType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryData extends MetricData {
  private double memoryUtilization; // Pct memory in use
  private long pageFaults; // during time span delta millis

  public MemoryData(double memoryUtilization, long pageFaults, long deltaMillis, long epochMillisTime) {
    this.memoryUtilization = memoryUtilization;
    this.pageFaults = pageFaults;
    this.deltaMillis = deltaMillis;
    this.epochMillisTime = epochMillisTime;
  }

  public double getMemoryUtilization() {
    return memoryUtilization;
  }

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
