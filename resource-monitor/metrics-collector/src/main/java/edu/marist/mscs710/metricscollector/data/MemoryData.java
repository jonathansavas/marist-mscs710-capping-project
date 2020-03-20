package edu.marist.mscs710.metricscollector.data;

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
}
