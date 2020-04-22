package edu.marist.mscs710.metricscollector.system;

import edu.marist.mscs710.metricscollector.MetricSource;
import edu.marist.mscs710.metricscollector.data.CpuCoreData;
import edu.marist.mscs710.metricscollector.data.CpuData;
import edu.marist.mscs710.metricscollector.data.MetricData;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Sensors;

import java.time.Instant;
import java.util.*;

/**
 * Represents a CPU of an operating system. Produces metrics on demand, keeping
 * the previous state of the CPU.
 */
public class Cpu implements MetricSource {
  private static final double ONE_GHZ = 1000000000.0;
  private CentralProcessor processor;
  private Sensors sensors;
  private long[][] prevProcTicks;
  private final boolean reportPhysicalCores;
  private final double cpuSpeed;
  private long lastCheckInMillis;

  /**
   * Constructs a new <tt>Cpu</tt>.
   *
   * @param reportPhysicalCores attempts to reduce CPU core usage metrics
   *                            to physical cores rather than logical cores. If
   *                            logical cores = physical cores * 2, passing <tt>true</tt>
   *                            will produce per physical core metrics
   */
  public Cpu(boolean reportPhysicalCores) {
    HardwareAbstractionLayer hardware = new SystemInfo().getHardware();
    this.processor = hardware.getProcessor();
    this.sensors = hardware.getSensors();

    int numPhysCores = processor.getPhysicalProcessorCount();
    int numLogCores = processor.getLogicalProcessorCount();
    this.reportPhysicalCores = numPhysCores * 2 == numLogCores && reportPhysicalCores;

    long freq = processor.getProcessorIdentifier().getVendorFreq();
    this.cpuSpeed = Math.round(freq * 100.0 / ONE_GHZ) / 100.0;

    this.prevProcTicks = processor.getProcessorCpuLoadTicks();
    this.lastCheckInMillis = Instant.now().toEpochMilli();
  }

  @Override
  public List<MetricData> getMetricData() {
    double[] cpuCoreUsages = getCpuCoreUsageSinceLastCheck();

    long curMillis = Instant.now().toEpochMilli();
    long deltaMillis = curMillis - lastCheckInMillis;
    lastCheckInMillis = curMillis;

    List<MetricData> metrics = new ArrayList<>();

    metrics.add(new CpuData(getTotalCpuUsage(cpuCoreUsages), getCpuTemp(), deltaMillis, curMillis));

    for (int i = 0; i < cpuCoreUsages.length; i++) {
      metrics.add(new CpuCoreData(i, cpuCoreUsages[i], deltaMillis, curMillis));
    }

    return metrics;
  }

  private double[] getCpuCoreUsageSinceLastCheck() {
    long[][] currentProcTicks = processor.getProcessorCpuLoadTicks();
    double[] logCoreUsages = processor.getProcessorCpuLoadBetweenTicks(prevProcTicks);
    prevProcTicks = currentProcTicks;

    // CPU % per core as double[] 0.0-1.0
    return reportPhysicalCores ? reduceToPhysicalCores(logCoreUsages) : logCoreUsages;
  }

  /**
   * Static method to reduce per core usage data to total CPU usage.
   *
   * @param perCoreUsages array of CPU core usages
   * @return total CPU usage
   */
  public static double getTotalCpuUsage(double[] perCoreUsages) {
    OptionalDouble avg = Arrays.stream(perCoreUsages).average();
    return avg.isPresent() ? avg.getAsDouble() : Double.NaN; // 0.0-1.0
  }

  /**
   * Gets the CPU temperature in degrees Celsius, or 0.0 if unavailable.
   *
   * @return CPU temperature
   */
  public double getCpuTemp() {
    return sensors.getCpuTemperature(); // CPU temp in Celsius
  }

  /**
   * Gets the speed in GHz of the processor.
   *
   * @return processor speed
   */
  public double getCpuSpeed() {
    return cpuSpeed; // GHz
  }

  private double[] reduceToPhysicalCores(double[] logCoreUsages) {
    int numLogCores = logCoreUsages.length;

    if (numLogCores % 2 != 0)
      return logCoreUsages;

    double[] physCoreUsages = new double[numLogCores / 2];
    for (int i = 0; i < physCoreUsages.length; i++) {
      physCoreUsages[i] = (logCoreUsages[i * 2] + logCoreUsages[i * 2 + 1]) / 2;
    }

    return physCoreUsages;
  }
}
