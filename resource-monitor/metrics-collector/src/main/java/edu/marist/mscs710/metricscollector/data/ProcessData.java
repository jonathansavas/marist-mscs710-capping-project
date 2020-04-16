package edu.marist.mscs710.metricscollector.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.marist.mscs710.metricscollector.metric.Fields;
import edu.marist.mscs710.metricscollector.metric.Metric;
import edu.marist.mscs710.metricscollector.metric.MetricType;
import edu.marist.mscs710.metricscollector.system.Processes;

import java.util.List;

/**
 * Holds a snapshot of Process data.
 */
public class ProcessData extends MetricData {
  @JsonIgnore
  private static final long BYTES_PER_KB = 1024L;

  @JsonProperty(Fields.METRIC_TYPE)
  private static final String metricType = MetricType.PROCESSES.toString().toLowerCase();

  @JsonProperty(Fields.PROCESSES_PID)
  private int pid;

  @JsonProperty(Fields.PROCESSES_NAME)
  private String name;

  @JsonProperty(Fields.PROCESSES_START_TIME)
  private long startTime;

  @JsonProperty(Fields.PROCESSES_UPTIME)
  private long upTime;

  @JsonProperty(Fields.PROCESSES_CPU_USAGE)
  private double cpuUsage; // During previous delta millis

  @JsonProperty(Fields.PROCESSES_MEMORY)
  private long memory; // Total bytes allocated to process and in RAM

  @JsonProperty(Fields.PROCESSES_KB_READ)
  private double kbRead; // kb read from disk per second during previous delta millis

  @JsonProperty(Fields.PROCESSES_KB_WRITTEN)
  private double kbWritten; // kb written to disk per second during previous delta millis

  @JsonProperty(Fields.PROCESSES_STATE)
  private Processes.PidState pidState;

  /**
   * Constructs a new <tt>ProcessData</tt> with the supplied metrics.
   *
   * @param pid             process id
   * @param name            name of the process
   * @param startTime       start time of process as epoch milli timestamp
   * @param upTime          number of milliseconds the process has been running
   * @param cpuUsage        cpu usage of the process during this snapshot
   * @param memory          total bytes allocated to this process and in RAM
   * @param bytesRead       total bytes read from disk during this snapshot
   * @param bytesWritten    total bytes written to disk during this snapshot
   * @param pidState        state of the process
   * @param deltaMillis     time covered by this snapshot
   * @param epochMillisTime epoch milli timestamp of this snapshot
   */
  public ProcessData(int pid, String name, long startTime, long upTime,
                     double cpuUsage, long memory, long bytesRead, long bytesWritten,
                     Processes.PidState pidState, long deltaMillis, long epochMillisTime) {
    this.pid = pid;
    this.name = name;
    this.startTime = startTime;
    this.upTime = upTime;
    this.cpuUsage = cpuUsage;
    this.memory = memory / BYTES_PER_KB;
    this.kbRead = ((double) bytesRead) / deltaMillis;
    this.kbWritten = ((double) bytesWritten) / deltaMillis;
    this.deltaMillis = deltaMillis;
    this.pidState = pidState;
    this.epochMillisTime = epochMillisTime;
  }

  /**
   * Constructs a new <tt>ProcessData</tt> with state <tt>Processes.PidState.ENDED</tt>
   *
   * @param pid             process id
   * @param name            name of the process
   * @param epochMillisTime epoch milli timestamp of this snapshot
   */
  public ProcessData(int pid, String name, long epochMillisTime) {
    this.pid = pid;
    this.name = name;
    this.startTime = -1;
    this.upTime = -1;
    this.cpuUsage = -1;
    this.memory = -1;
    this.kbRead = -1;
    this.kbWritten = -1;
    this.deltaMillis = -1;
    this.pidState = Processes.PidState.ENDED;
    this.epochMillisTime = epochMillisTime;
  }

  /**
   * Gets the process id.
   *
   * @return process id
   */
  public int getPid() {
    return pid;
  }

  /**
   * Gets the process name.
   *
   * @return process name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the process start time as epoch milli timestamp.
   *
   * @return start time
   */
  public long getStartTime() {
    return startTime;
  }

  /**
   * Gets number of milliseconds the process has been running.
   *
   * @return milliseconds of uptime
   */
  public long getUpTime() {
    return upTime;
  }

  /**
   * Gets the cpu usage of the process during this snapshot.
   *
   * @return cpu usage
   */
  public double getCpuUsage() {
    return cpuUsage;
  }

  /**
   * Gets the total number of bytes allocated to this process and in RAM
   *
   * @return bytes of memory
   */
  public long getMemory() {
    return memory;
  }

  /**
   * Gets the total number of bytes read from disk during this snapshot.
   *
   * @return bytes read
   */
  public double getKbRead() {
    return kbRead;
  }

  /**
   * Gets the total number of bytes written to disk during this snapshot.
   *
   * @return bytes read
   */
  public double getKbWritten() {
    return kbWritten;
  }

  /**
   * Gets the state of the process.
   *
   * @return process state
   */
  public Processes.PidState getPidState() {
    return pidState;
  }

  @Override
  public String toString() {
    return "ProcessData{" +
      "pid=" + pid +
      ", name='" + name + '\'' +
      ", startTime=" + startTime +
      ", upTime=" + upTime +
      ", cpuUsage=" + cpuUsage +
      ", memory=" + memory +
      ", kbRead=" + kbRead +
      ", kbWritten=" + kbWritten +
      ", pidState=" + pidState +
      ", deltaMillis=" + deltaMillis +
      ", epochMillisTime=" + epochMillisTime +
      '}';
  }

  @Override
  public List<Metric> toMetricRecords() {
    return null;
  }
}
