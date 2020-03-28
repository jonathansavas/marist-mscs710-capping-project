package edu.marist.mscs710.metricscollector.metric;

/**
 * Specifies fields for the <tt>metricData</tt> associated with a
 * <tt>Metric</tt> object.
 */
public class Fields {

  /**
   * <tt>MetricType.CPU</tt> fields
   */
  public enum Cpu {
    /**
     * Epoch milli timestamp of this snapshot of metric data
     */
    DATETIME,
    /**
     * Time covered by this snapshot
     */
    DELTA_MILLIS,
    /**
     * CPU temperature in degrees Celsius, 0.0 if not available
     */
    TEMPERATURE,
    /**
     * CPU utilization during this snapshot from 0.0-1.0
     */
    UTILIZATION
  }

  /**
   * <tt>MetricType.CPU_CORE</tt> fields
   */
  public enum CpuCore {
    /**
     * Epoch milli timestamp of this snapshot of metric data
     */
    DATETIME,
    /**
     * Time covered by this snapshot
     */
    DELTA_MILLIS,
    /**
     * The CPU core id
     */
    CORE_ID,
    /**
     * The CPU core utilization during this snapshot, from 0.0-1.0
     */
    CORE_UTILIZATION,
  }

  /**
   * <tt>MetricType.MEMORY</tt> fields
   */
  public enum Memory {
    /**
     * Epoch milli timestamp of this snapshot of metric data
     */
    DATETIME,
    /**
     * Time covered by this snapshot
     */
    DELTA_MILLIS,
    /**
     * Memory utilization from 0.0-1.0
     */
    UTILIZATION,
    /**
     * Number of page faults per second during this snapshot
     */
    PAGE_FAULTS
  }

  /**
   * <tt>MetricType.NETWORK</tt> fields
   */
  public enum Network {
    /**
     * Epoch milli timestamp of this snapshot of metric data
     */
    DATETIME,
    /**
     * Time covered by this snapshot
     */
    DELTA_MILLIS,
    /**
     * Kilobits per second network capacity, the sum over active networks during
     * this snapshot. A network interface is considered inactive after five
     * minutes of inactivity.
     */
    THROUGHPUT,
    /**
     * Kilobits per second sent during this snapshot
     */
    SEND,
    /**
     * Kilobits per second received during this snapshot
     */
    RECEIVE
  }

  /**
   * <tt>MetricType.PROCESSES</tt> fields
   */
  public enum Processes {
    /**
     * Epoch milli timestamp of this snapshot of metric data
     */
    DATETIME,
    /**
     * Time covered by this snapshot
     */
    DELTA_MILLIS,
    /**
     * Process id
     */
    PID,
    /**
     * Process name
     */
    NAME,
    /**
     * Start time of process as epoch milli timestamp
     */
    START_TIME,
    /**
     * Time the process has been running in milliseconds
     */
    UPTIME,
    /**
     * CPU usage of the process during this snapshot
     */
    CPU_USAGE,
    /**
     * Number of kilobytes of memory allocated to this process and in RAM
     */
    MEMORY,
    /**
     * Kilobytes per second read from disk during this snapshot
     */
    KB_READ,
    /**
     * Kilobytes per second written to disk during this snapshot
     */
    KB_WRITTEN,
    /**
     * Process state
     */
    STATE
  }

  /**
   * <tt>MetricType.SYSTEM_METRICS</tt> fields
   */
  public enum SystemMetrics {
    /**
     * Epoch milli timestamp of this snapshot of metric data
     */
    DATETIME,
    /**
     * Number of seconds since system boot
     */
    UPTIME
  }

  /**
   * <tt>MetricType.SYSTEM_CONSTANTS</tt> fields
   */
  public enum SystemConstants {
    /**
     * Total memory of the system in gigabytes
     */
    TOTAL_MEMORY,
    /**
     * Number of physical CPU cores of the system
     */
    PHYSICAL_CORES,
    /**
     * Number of logical CPU cores of the system
     */
    LOGICAL_CORES,
    /**
     * Processor speed in GHz
     */
    CPU_SPEED // GHz
  }
}
