package edu.marist.mscs710.metricscollector.system;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

public class SystemConstants {
  private static long totalMemBytes;
  private static double totalMemGb;
  private static int physicalCores;
  private static int logicalCores;
  private static double cpuSpeed; // GHz

  public SystemConstants() {
    Memory mem = new Memory();
    totalMemBytes = mem.getTotalMemoryInBytes();
    totalMemGb = mem.getTotalMemoryInGb();

    cpuSpeed = new Cpu(true).getCpuSpeed();

    CentralProcessor processor = new SystemInfo().getHardware().getProcessor();
    physicalCores = processor.getPhysicalProcessorCount();
    logicalCores = processor.getLogicalProcessorCount();
  }

  public static long getTotalMemBytes() {
    return totalMemBytes;
  }

  public static double getTotalMemGb() {
    return totalMemGb;
  }

  public static int getPhysicalCores() {
    return physicalCores;
  }

  public static int getLogicalCores() {
    return logicalCores;
  }

  public static double getCpuSpeed() {
    return cpuSpeed;
  }
}
