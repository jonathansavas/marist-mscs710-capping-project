package edu.marist.mscs710.metricscollector.data;

public class SystemData extends MetricData {
  private long upTime; // Seconds since system boot

  public SystemData(long upTime, long deltaMillis, long epochMillisTime) {
    this.upTime = upTime;
    this.deltaMillis = deltaMillis;
    this.epochMillisTime = epochMillisTime;
  }

  public long getUpTime() {
    return upTime;
  }

  @Override
  public String toString() {
    return "SystemData{" +
      "upTime=" + upTime +
      ", deltaMillis=" + deltaMillis +
      ", epochMillisTime=" + epochMillisTime +
      '}';
  }
}
