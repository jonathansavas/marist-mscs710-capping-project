package edu.marist.mscs710.metricscollector.data;

import edu.marist.mscs710.metricscollector.metric.Fields;
import edu.marist.mscs710.metricscollector.metric.Metric;
import edu.marist.mscs710.metricscollector.metric.MetricType;

import java.util.*;

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

  private Map<String, Object> getCpuMap() {
    return new HashMap<String, Object>() {
      {
        put(Fields.Cpu.DATETIME.toString(), epochMillisTime);
        put(Fields.Cpu.TEMPERATURE.toString(), cpuTemp);
        put(Fields.Cpu.UTILIZATION.toString(), totalCpuUsage);
      }
    };
  }

  private List<Map<String, Object>> getCpuCoreMaps() {
    int numCores = cpuCoreUsages.length;
    List<Map<String, Object>> coreMaps = new ArrayList<>(numCores);

    for (int i = 0; i < numCores; i++) {
      int coreId = i;
      coreMaps.add(new HashMap<String, Object>() {
        {
          put(Fields.CpuCore.DATETIME.toString(), epochMillisTime);
          put(Fields.CpuCore.CORE_ID.toString(), coreId);
          put(Fields.CpuCore.CORE_UTILIZATION.toString(), cpuCoreUsages[coreId]);
        }
      });
    }

    return coreMaps;
  }

  @Override
  public List<Metric> toMetricRecords() {
    List<Metric> cpuRecords = new ArrayList<>();

    cpuRecords.add(new Metric(MetricType.CPU, getCpuMap()));

    for (Map<String, Object> cpuCoreMap : getCpuCoreMaps()) {
      cpuRecords.add(new Metric(MetricType.CPU_CORE, cpuCoreMap));
    }

    return cpuRecords;
  }
}
