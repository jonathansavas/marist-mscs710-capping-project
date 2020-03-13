package edu.marist.mscs710.metricscollector.data;

public class CpuData extends MetricData {
  private double[] cpuCoreUsages;
  private double totalCpuUsage;
  private double cpuTemp;

  public CpuData(double[] cpuCoreUsages, double totalCpuUsage, double cpuTemp, long deltaMillis) {
    this.cpuCoreUsages = cpuCoreUsages;
    this.totalCpuUsage = totalCpuUsage;
    this.cpuTemp = cpuTemp;
    this.deltaMillis = deltaMillis;
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
}
