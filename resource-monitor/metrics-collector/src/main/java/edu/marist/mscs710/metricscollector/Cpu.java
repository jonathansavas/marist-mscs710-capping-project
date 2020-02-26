package edu.marist.mscs710.metricscollector;

import oshi.hardware.common.AbstractCentralProcessor;
import oshi.hardware.common.AbstractSensors;
import oshi.hardware.platform.linux.LinuxCentralProcessor;
import oshi.hardware.platform.linux.LinuxSensors;
import oshi.hardware.platform.windows.WindowsCentralProcessor;
import oshi.hardware.platform.windows.WindowsSensors;

public class Cpu {
  private AbstractCentralProcessor processor;
  private AbstractSensors sensors;
  private long[] PREV_TICKS;

  public Cpu(SystemType system) {
    if (system == SystemType.LINUX) {
      processor = new LinuxCentralProcessor();
      sensors = new LinuxSensors();
    } else {
      processor = new WindowsCentralProcessor();
      sensors = new WindowsSensors();
    }

    PREV_TICKS = processor.getSystemCpuLoadTicks();
  }

  public double getSystemCpuUsageSinceLastCheck() {
    long[] currentTicks = processor.getSystemCpuLoadTicks();
    double systemCpu = processor.getSystemCpuLoadBetweenTicks(PREV_TICKS);
    PREV_TICKS = currentTicks;
    return systemCpu; // CPU % as double 0.0-1.0
  }

  public double getCpuTemp() {
    return sensors.getCpuTemperature(); // CPU temp in Celsius
  }
}
