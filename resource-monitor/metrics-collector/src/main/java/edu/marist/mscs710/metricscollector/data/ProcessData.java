package edu.marist.mscs710.metricscollector.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.marist.mscs710.metricscollector.metric.Fields;
import edu.marist.mscs710.metricscollector.system.Processes;

import static edu.marist.mscs710.metricscollector.utils.DataUtils.weightedAverage;

import java.util.*;

/**
 * Holds a snapshot of Process data.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessData extends MetricData {
  public static final String SQL_INSERT_PREFIX = "INSERT INTO " +
    Fields.METRIC_TYPE_PROCESSES + " (" +
    Fields.PROCESSES_DATETIME + ',' +
    Fields.PROCESSES_DELTA_MILLIS + ',' +
    Fields.PROCESSES_PID + ',' +
    Fields.PROCESSES_NAME + ',' +
    Fields.PROCESSES_START_TIME + ',' +
    Fields.PROCESSES_UPTIME + ',' +
    Fields.PROCESSES_CPU_USAGE + ',' +
    Fields.PROCESSES_MEMORY + ',' +
    Fields.PROCESSES_KB_READ + ',' +
    Fields.PROCESSES_KB_WRITTEN + ',' +
    Fields.PROCESSES_STATE + ") VALUES ";

  private static final long BYTES_PER_KB = 1024L;

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

  @JsonCreator
  public ProcessData(@JsonProperty(Fields.PROCESSES_PID) int pid,
                     @JsonProperty(Fields.PROCESSES_NAME) String name,
                     @JsonProperty(Fields.PROCESSES_START_TIME) long startTime,
                     @JsonProperty(Fields.PROCESSES_UPTIME) long upTime,
                     @JsonProperty(Fields.PROCESSES_CPU_USAGE) double cpuUsage,
                     @JsonProperty(Fields.PROCESSES_MEMORY) long memory,
                     @JsonProperty(Fields.PROCESSES_KB_READ) double kbRead,
                     @JsonProperty(Fields.PROCESSES_KB_WRITTEN) double kbWritten,
                     @JsonProperty(Fields.PROCESSES_STATE) Processes.PidState pidState,
                     @JsonProperty(Fields.DELTA_MILLIS)long deltaMillis,
                     @JsonProperty(Fields.DATETIME) long epochMillisTime) {
    this.pid = pid;
    this.name = name;
    this.startTime = startTime;
    this.upTime = upTime;
    this.cpuUsage = cpuUsage;
    this.memory = memory;
    this.kbRead = kbRead;
    this.kbWritten = kbWritten;
    this.deltaMillis = deltaMillis;
    this.pidState = pidState;
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

  /**
   * Sets the state of the process.
   * @param pidState process state
   */
  public void setPidState(Processes.PidState pidState) {
    this.pidState = pidState;
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ProcessData that = (ProcessData) o;
    return pid == that.pid &&
      startTime == that.startTime &&
      upTime == that.upTime &&
      Double.compare(that.cpuUsage, cpuUsage) == 0 &&
      memory == that.memory &&
      Double.compare(that.kbRead, kbRead) == 0 &&
      Double.compare(that.kbWritten, kbWritten) == 0 &&
      name.equals(that.name) &&
      pidState == that.pidState &&
      deltaMillis == that.deltaMillis &&
      epochMillisTime == that.epochMillisTime;
  }

  @Override
  public String toSqlInsertString() {
    return SQL_INSERT_PREFIX + '(' +
      epochMillisTime + ',' +
      deltaMillis + ',' +
      pid + ',' +
      '\'' + name + '\'' + ',' +
      startTime + ',' +
      upTime + ',' +
      cpuUsage + ',' +
      memory + ',' +
      kbRead + ',' +
      kbWritten + ',' +
      '\'' + pidState + '\'' + ')' + ';';
  }

  public static List<ProcessData> combine(List<ProcessData> metrics) {
    List<ProcessData> sortedMetrics = sortChronologically(metrics);

    List<ProcessData> combinedMetrics = new ArrayList<>();
    Map<Integer, List<ProcessData>> metricsByPid = new HashMap<>();

    for (ProcessData data : sortedMetrics) {
      switch (data.getPidState()) {
        case NEW:
        case RECYCLED:
          ProcessData combined = combineSameProcessMetrics(
            metricsByPid.get(data.getPid())
          );

          if (combined != null)
            combinedMetrics.add(combined);

          metricsByPid.put(data.getPid(), new ArrayList<>(Collections.singleton(data)));
          break;
        case RUNNING:
          metricsByPid.computeIfAbsent(data.getPid(), ArrayList::new).add(data);
          break;
        case ENDED:
          combinedMetrics.add(data);
          break;
      }
    }

    for (List<ProcessData> sameProcessMetrics : metricsByPid.values()) {
      ProcessData combined = combineSameProcessMetrics(sameProcessMetrics);

      if (combined != null)
        combinedMetrics.add(combined);
    }

    return sortChronologically(combinedMetrics);
  }

  private static ProcessData combineSameProcessMetrics(List<ProcessData> sameProcessMetrics) {
    if (sameProcessMetrics == null || sameProcessMetrics.isEmpty())
      return null;

    // Want to keep earliest pid state
    ProcessData first = sortChronologically(sameProcessMetrics).get(0);

    int pid = first.getPid();
    String name = first.getName();
    long startTime = first.getStartTime();
    Processes.PidState pidState = first.getPidState();

    double datetime = 0;
    long totalMillis = 0;
    double upTime = 0;
    double cpuUsage = 0;
    double memory = 0;
    double kbRead = 0;
    double kbWritten = 0;

    for (ProcessData data : sameProcessMetrics) {
      long deltaMillis = data.getDeltaMillis();
      datetime = weightedAverage(datetime, totalMillis, data.getEpochMillisTime(), deltaMillis);
      upTime = weightedAverage(upTime, totalMillis, data.getUpTime(), deltaMillis);
      cpuUsage = weightedAverage(cpuUsage, totalMillis, data.getCpuUsage(), deltaMillis);
      memory = weightedAverage(memory, totalMillis, data.getMemory(), deltaMillis);
      kbRead = weightedAverage(kbRead, totalMillis, data.getKbRead(), deltaMillis);
      kbWritten = weightedAverage(kbWritten, totalMillis, data.getKbWritten(), deltaMillis);
      totalMillis += deltaMillis;
    }

    return new ProcessData(pid, name, startTime, (long) upTime, cpuUsage, (long) memory, kbRead, kbWritten, pidState, totalMillis, (long) datetime);
  }
}
