package edu.marist.mscs710.metricscollector.metric;

public class Fields {

  public enum Cpu {
    DATETIME, // Unix Time in millis
    DELTA_MILLIS,
    TEMPERATURE, // Celsius
    UTILIZATION // Pct utilization since last update
  }

  public enum CpuCore {
    DATETIME, // Unix Time in millis
    DELTA_MILLIS,
    CORE_ID,
    CORE_UTILIZATION, // Pct utilization since last update
  }

  public enum Memory {
    DATETIME, // Unix Time in millis
    DELTA_MILLIS,
    UTILIZATION, // Pct of memory in use
    PAGE_FAULTS // Page faults / sec since last update
  }

  public enum Network {
    DATETIME, // Unix Time in millis
    DELTA_MILLIS,
    THROUGHPUT, // kb / s network capacity, sum over active networks since last update
    SEND, // kb / s sent since last update
    RECEIVE // kb / s received since last update
  }

  public enum Processes {
    DATETIME, // Unix Time in millis
    DELTA_MILLIS,
    PID,
    NAME,
    START_TIME, // Unix Time in millis
    UPTIME, // Millis
    CPU_USAGE, // Pct utilization since last update
    MEMORY, // Total KB allocated to process and in RAM
    KB_READ, // KB read from disk per second since last update
    KB_WRITTEN, // KB written to disk per second since last update
    STATE // Process State
  }

  public enum SystemMetrics {
    DATETIME, // Unix Time in millis
    UPTIME // Seconds since boot time
  }

  public enum SystemConstants {
    TOTAL_MEMORY, // GB
    PHYSICAL_CORES,
    LOGICAL_CORES,
    CPU_SPEED // GHz
  }
}
