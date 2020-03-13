package edu.marist.mscs710.metricscollector;

import edu.marist.mscs710.metricscollector.data.CpuData;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Sensors;

import java.time.Instant;
import java.util.Arrays;
import java.util.OptionalDouble;

public class Cpu {
  private static final double ONE_GHZ = 1000000000.0;
  private CentralProcessor processor;
  private Sensors sensors;
  private long[][] prevProcTicks;
  private final boolean reportPhysicalCores;
  private final double cpuSpeed;
  private long lastCheckInMillis;

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

  public CpuData getCpuData() {
    double[] cpuCoreUsages = getCpuCoreUsageSinceLastCheck();

    long curMillis = Instant.now().toEpochMilli();
    long deltaMillis = curMillis - lastCheckInMillis;
    lastCheckInMillis = curMillis;

    return new CpuData(cpuCoreUsages, getTotalCpuUsage(cpuCoreUsages),
                       getCpuTemp(), deltaMillis);
  }

  private double[] getCpuCoreUsageSinceLastCheck() {
    long[][] currentProcTicks = processor.getProcessorCpuLoadTicks();
    double[] logCoreUsages = processor.getProcessorCpuLoadBetweenTicks(prevProcTicks);
    prevProcTicks = currentProcTicks;

    // CPU % per core as double[] 0.0-1.0
    return reportPhysicalCores ? reduceToPhysicalCores(logCoreUsages) : logCoreUsages;
  }

  public static double getTotalCpuUsage(double[] perCoreUsages) {
    OptionalDouble avg = Arrays.stream(perCoreUsages).average();
    return avg.isPresent() ? avg.getAsDouble() : Double.NaN; // 0.0-1.0
  }

  public double getCpuTemp() {
    return sensors.getCpuTemperature(); // CPU temp in Celsius
  }

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
