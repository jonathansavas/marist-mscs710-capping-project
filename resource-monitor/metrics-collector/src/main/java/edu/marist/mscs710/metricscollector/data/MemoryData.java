package edu.marist.mscs710.metricscollector.data;

public class MemoryData extends MetricData {
  private double memoryUtilization;
  private long pageFaults;

  public MemoryData(double memoryUtilization, long pageFaults, long deltaMillis) {
    this.memoryUtilization = memoryUtilization;
    this.pageFaults = pageFaults;
    this.deltaMillis = deltaMillis;
  }

  public double getMemoryUtilization() {
    return memoryUtilization;
  }

  public long getPageFaults() {
    return pageFaults;
  }
}
