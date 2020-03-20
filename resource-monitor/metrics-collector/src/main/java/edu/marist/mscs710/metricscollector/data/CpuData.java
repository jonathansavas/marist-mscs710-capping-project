package edu.marist.mscs710.metricscollector.data;

import java.util.Arrays;

public class CpuData extends MetricData {
  private double[] cpuCoreUsages; // Per core cpu usage during previous delta millis
  private double totalCpuUsage; // Total cpu usage during previous delta millis
  private double cpuTemp; // degrees C, 0.0 if not available, need elevated permissions in Windows

  public CpuData(double[] cpuCoreUsages, double totalCpuUsage, double cpuTemp, long deltaMillis, long epochMillisTime) {
    this.cpuCoreUsages = cpuCoreUsages;
    this.totalCpuUsage = totalCpuUsage;
    this.cpuTemp = cpuTemp;
    this.deltaMillis = deltaMillis;
    this.epochMillisTime = epochMillisTime;
  }

  public double[] getCpuCoreUsages() {
    return cpuCoreUsages;
  }

  public double getTotalCpuUsage() {
    return totalCpuUsage;
  }

  public double getCpuTemp() {
    return cpuTemp;
  }

  @Override
  public String toString() {
    return "CpuData{" +
      "cpuCoreUsages=" + Arrays.toString(cpuCoreUsages) +
      ", totalCpuUsage=" + totalCpuUsage +
      ", cpuTemp=" + cpuTemp +
      ", deltaMillis=" + deltaMillis +
      ", epochMillisTime=" + epochMillisTime +
      '}';
  }
}
