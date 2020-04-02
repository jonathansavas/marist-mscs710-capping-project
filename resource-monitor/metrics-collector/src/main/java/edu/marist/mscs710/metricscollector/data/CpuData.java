package edu.marist.mscs710.metricscollector.data;

import edu.marist.mscs710.metricscollector.metric.Fields;
import edu.marist.mscs710.metricscollector.metric.Metric;
import edu.marist.mscs710.metricscollector.metric.MetricType;

import java.util.*;

/**
 * Holds a snapshot of CPU data. CPU usage metrics are given as percent
 * utilization from 0.0-1.0.
 */
public class CpuData extends MetricData {
  private double[] cpuCoreUsages; // Per core cpu usage during previous delta millis
  private double totalCpuUsage; // Total cpu usage during previous delta millis
  private double cpuTemp; // degrees C, 0.0 if not available, need elevated permissions in Windows

  /**
   * Constructs a new <tt>CpuData</tt> with the supplied metrics.
   *
   * @param cpuCoreUsages   array of doubles representing CPU usages during this snapshot
   * @param totalCpuUsage   overall CPU usage during this snapshot
   * @param cpuTemp         cpu temperature in degrees Celsius
   * @param deltaMillis     time covered by this snapshot
   * @param epochMillisTime epoch milli timestamp of this snapshot
   */
  public CpuData(double[] cpuCoreUsages, double totalCpuUsage, double cpuTemp, long deltaMillis, long epochMillisTime) {
    this.cpuCoreUsages = cpuCoreUsages;
    this.totalCpuUsage = totalCpuUsage;
    this.cpuTemp = cpuTemp;
    this.deltaMillis = deltaMillis;
    this.epochMillisTime = epochMillisTime;
  }

  /**
   * Gets the array of CPU core usages. ID is given by array index.
   *
   * @return double[] of cpu core usages
   */
  public double[] getCpuCoreUsages() {
    return cpuCoreUsages;
  }

  /**
   * Gets the overall CPU usage.
   *
   * @return overall CPU usage
   */
  public double getTotalCpuUsage() {
    return totalCpuUsage;
  }

  /**
   * Gets the CPU temperature.
   *
   * @return CPU temperature in degrees Celsius, at epochMillisTime,
   * or 0.0 if unavailable
   */
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
        put(Fields.Cpu.DELTA_MILLIS.toString(), deltaMillis);
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
          put(Fields.CpuCore.DELTA_MILLIS.toString(), deltaMillis);
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
