package edu.marist.mscs710.metricscollector.system;

import edu.marist.mscs710.metricscollector.MetricRecord;
import edu.marist.mscs710.metricscollector.metric.Fields;
import edu.marist.mscs710.metricscollector.metric.Metric;
import edu.marist.mscs710.metricscollector.metric.MetricType;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemConstants implements MetricRecord {
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

  private Map<String, Object> getSystemConstantMap() {
    return new HashMap<String, Object>() {
      {
        put(Fields.SystemConstants.CPU_SPEED.toString(), cpuSpeed);
        put(Fields.SystemConstants.LOGICAL_CORES.toString(), logicalCores);
        put(Fields.SystemConstants.PHYSICAL_CORES.toString(), physicalCores);
        put(Fields.SystemConstants.TOTAL_MEMORY.toString(), totalMemGb);
      }
    };
  }

  @Override
  public List<Metric> toMetricRecords() {
    return Collections.singletonList(new Metric(MetricType.SYSTEM_CONSTANTS, getSystemConstantMap()));
  }
}
