package edu.marist.mscs710.metricscollector.system;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.marist.mscs710.metricscollector.Metric;
import edu.marist.mscs710.metricscollector.metric.Fields;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

/**
 * Class to hold the constant values for a particular system.
 */
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  property = Fields.METRIC_TYPE
)
@JsonTypeName(Fields.METRIC_TYPE_SYSTEM_CONSTANTS)
public class SystemConstants implements Metric {
  public static final String SQL_INSERT_PREFIX = "INSERT INTO " +
    Fields.METRIC_TYPE_SYSTEM_CONSTANTS + " (" +
    Fields.SYSTEM_CONSTANTS_TOTAL_MEMORY + ',' +
    Fields.SYSTEM_CONSTANTS_PHYSICAL_CORES + ',' +
    Fields.SYSTEM_CONSTANTS_LOGICAL_CORES + ',' +
    Fields.SYSTEM_CONSTANTS_CPU_SPEED + ") VALUES ";

  @JsonProperty(Fields.SYSTEM_CONSTANTS_TOTAL_MEMORY)
  private double totalMemGb;

  @JsonProperty(Fields.SYSTEM_CONSTANTS_PHYSICAL_CORES)
  private int physicalCores;

  @JsonProperty(Fields.SYSTEM_CONSTANTS_LOGICAL_CORES)
  private int logicalCores;

  @JsonProperty(Fields.SYSTEM_CONSTANTS_CPU_SPEED)
  private double cpuSpeed;

  /**
   * Constructs a new <tt>SystemConstants</tt>
   */
  public SystemConstants() {
    Memory mem = new Memory();
    totalMemGb = mem.getTotalMemoryInGb();

    cpuSpeed = new Cpu(true).getCpuSpeed();

    CentralProcessor processor = new SystemInfo().getHardware().getProcessor();
    physicalCores = processor.getPhysicalProcessorCount();
    logicalCores = processor.getLogicalProcessorCount();
  }

  @JsonCreator
  public SystemConstants(@JsonProperty(Fields.SYSTEM_CONSTANTS_TOTAL_MEMORY) double totalMemGb,
                         @JsonProperty(Fields.SYSTEM_CONSTANTS_PHYSICAL_CORES) int physicalCores,
                         @JsonProperty(Fields.SYSTEM_CONSTANTS_LOGICAL_CORES) int logicalCores,
                         @JsonProperty(Fields.SYSTEM_CONSTANTS_CPU_SPEED) double cpuSpeed) {
    this.totalMemGb = totalMemGb;
    this.physicalCores = physicalCores;
    this.logicalCores = logicalCores;
    this.cpuSpeed = cpuSpeed;
  }

  /**
   * Gets the total memory of the system, in gigabytes.
   *
   * @return total memory
   */
  public double getTotalMemGb() {
    return totalMemGb;
  }

  /**
   * Gets the number of physical cores of the system.
   *
   * @return number of physical cores
   */
  public int getPhysicalCores() {
    return physicalCores;
  }

  /**
   * Gets the number of logical cores of the system.
   *
   * @return number of logical cores
   */
  public int getLogicalCores() {
    return logicalCores;
  }

  /**
   * Gets the processor speed in GHz.
   *
   * @return processor speed
   */
  public double getCpuSpeed() {
    return cpuSpeed;
  }


  @Override
  public String toSqlInsertString() {
    return SQL_INSERT_PREFIX + '(' +
      totalMemGb + ',' +
      physicalCores + ',' +
      logicalCores + ',' +
      cpuSpeed + ')' + ';';
  }
}
