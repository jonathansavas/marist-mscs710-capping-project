package edu.marist.mscs710.metricscollector.metric;

import java.util.Arrays;
import java.util.List;

/**
 * Specifies fields for the <tt>metricData</tt> associated with a
 * <tt>Metric</tt> object.
 */
public final class Fields {
  public static final String METRIC_TYPE = "metric_type";

  public static final String METRIC_TYPE_CPU = "cpu";

  public static final String METRIC_TYPE_CPU_CORE = "cpu_core";

  public static final String METRIC_TYPE_MEMORY = "memory";

  public static final String METRIC_TYPE_NETWORK = "network";

  public static final String METRIC_TYPE_PROCESSES = "processes";

  public static final String METRIC_TYPE_SYSTEM_CONSTANTS = "system_constants";

  public static final String METRIC_TYPE_SYSTEM_METRICS = "system_metrics";



  public static final String DATETIME = "datetime";

  public static final String DELTA_MILLIS = "delta_millis";



  /**
   * Epoch milli timestamp of this snapshot of metric data
   */
  public static final String CPU_DATETIME = DATETIME;

  /**
   * Time covered by this snapshot
   */
  public static final String CPU_DELTA_MILLIS = DELTA_MILLIS;

  /**
   * CPU temperature in degrees Celsius, 0.0 if not available
   */
  public static final String CPU_TEMPERATURE = "temperature";

  /**
   * CPU utilization during this snapshot from 0.0-1.0
   */
  public static final String CPU_UTILIZATION = "utilization";

  public static final List<String> CPU_FIELDS = Arrays.asList(CPU_DATETIME, CPU_DELTA_MILLIS, CPU_TEMPERATURE, CPU_UTILIZATION);



  /**
   * Epoch milli timestamp of this snapshot of metric data
   */
  public static final String CPU_CORE_DATETIME = DATETIME;

  /**
   * Time covered by this snapshot
   */
  public static final String CPU_CORE_DELTA_MILLIS = DELTA_MILLIS;

  /**
   * The CPU core id
   */
  public static final String CPU_CORE_CORE_ID = "core_id";

  /**
   * The CPU core utilization during this snapshot, from 0.0-1.0
   */
  public static final String CPU_CORE_CORE_UTILIZATION = "core_utilization";

  public static final List<String> CPU_CORE_FIELDS = Arrays.asList(CPU_CORE_DATETIME, CPU_CORE_DELTA_MILLIS, CPU_CORE_CORE_ID, CPU_CORE_CORE_UTILIZATION);



  /**
   * Epoch milli timestamp of this snapshot of metric data
   */
  public static final String MEMORY_DATETIME = DATETIME;

  /**
   * Time covered by this snapshot
   */
  public static final String MEMORY_DELTA_MILLIS = DELTA_MILLIS;

  /**
   * Memory utilization from 0.0-1.0
   */
  public static final String MEMORY_UTILIZATION = "utilization";

  /**
   * Number of page faults per second during this snapshot
   */
  public static final String MEMORY_PAGE_FAULTS = "page_faults";

  public static final List<String> MEMORY_FIELDS = Arrays.asList(MEMORY_DATETIME, MEMORY_DELTA_MILLIS, MEMORY_UTILIZATION, MEMORY_PAGE_FAULTS);



  /**
   * Epoch milli timestamp of this snapshot of metric data
   */
  public static final String NETWORK_DATETIME = DATETIME;

  /**
   * Time covered by this snapshot
   */
  public static final String NETWORK_DELTA_MILLIS = DELTA_MILLIS;

  /**
   * Kilobits per second network capacity, the sum over active networks during
   * this snapshot. A network interface is considered inactive after five
   * minutes of inactivity.
   */
  public static final String NETWORK_THROUGHPUT = "throughput";

  /**
   * Kilobits per second sent during this snapshot
   */
  public static final String NETWORK_SEND = "send";

  /**
   * Kilobits per second received during this snapshot
   */
  public static final String NETWORK_RECEIVE = "receive";

  public static final List<String> NETWORK_FIELDS = Arrays.asList(NETWORK_DATETIME, NETWORK_DELTA_MILLIS, NETWORK_THROUGHPUT, NETWORK_SEND, NETWORK_RECEIVE);


  /**
   * Epoch milli timestamp of this snapshot of metric data
   */
  public static final String PROCESSES_DATETIME = DATETIME;

  /**
   * Time covered by this snapshot
   */
  public static final String PROCESSES_DELTA_MILLIS = DELTA_MILLIS;

  /**
   * Process id
   */
  public static final String PROCESSES_PID = "pid";

  /**
   * Process name
   */
  public static final String PROCESSES_NAME = "name";

  /**
   * Start time of process as epoch milli timestamp
   */
  public static final String PROCESSES_START_TIME = "start_time";

  /**
   * Time the process has been running in milliseconds
   */
  public static final String PROCESSES_UPTIME = "uptime";

  /**
   * CPU usage of the process during this snapshot
   */
  public static final String PROCESSES_CPU_USAGE = "cpu_usage";

  /**
   * Number of kilobytes of memory allocated to this process and in RAM
   */
  public static final String PROCESSES_MEMORY = "memory";

  /**
   * Kilobytes per second read from disk during this snapshot
   */
  public static final String PROCESSES_KB_READ = "kb_read";

  /**
   * Kilobytes per second written to disk during this snapshot
   */
  public static final String PROCESSES_KB_WRITTEN = "kb_written";

  /**
   * Process state
   */
  public static final String PROCESSES_STATE = "state";

  public static final List<String> PROCESSES_FIELDS = Arrays.asList(PROCESSES_DATETIME, PROCESSES_DELTA_MILLIS, PROCESSES_PID, PROCESSES_NAME, PROCESSES_START_TIME,
    PROCESSES_UPTIME, PROCESSES_CPU_USAGE, PROCESSES_MEMORY, PROCESSES_KB_READ, PROCESSES_KB_WRITTEN, PROCESSES_STATE);



  /**
   * Epoch milli timestamp of this snapshot of metric data
   */
  public static final String SYSTEM_METRICS_DATETIME = DATETIME;

  /**
   * Time covered by this snapshot
   */
  public static final String SYSTEM_METRICS_DELTA_MILLIS = DELTA_MILLIS;

  /**
   * Number of seconds since system boot
   */
  public static final String SYSTEM_METRICS_UPTIME = "uptime";

  public static final List<String> SYSTEM_METRICS_FIELDS = Arrays.asList(SYSTEM_METRICS_DATETIME, SYSTEM_METRICS_DELTA_MILLIS, SYSTEM_METRICS_UPTIME);



  /**
   * Total memory of the system in gigabytes
   */
  public static final String SYSTEM_CONSTANTS_TOTAL_MEMORY = "total_memory";

  /**
   * Number of physical CPU cores of the system
   */
  public static final String SYSTEM_CONSTANTS_PHYSICAL_CORES = "physical_cores";

  /**
   * Number of logical CPU cores of the system
   */
  public static final String SYSTEM_CONSTANTS_LOGICAL_CORES = "logical_cores";

  /**
   * Processor speed in GHz
   */
  public static final String SYSTEM_CONSTANTS_CPU_SPEED = "cpu_speed";

  public static final List<String> SYSTEM_CONSTANTS_FIELDS = Arrays.asList(SYSTEM_CONSTANTS_TOTAL_MEMORY, SYSTEM_CONSTANTS_PHYSICAL_CORES,
    SYSTEM_CONSTANTS_LOGICAL_CORES, SYSTEM_CONSTANTS_CPU_SPEED);

}
