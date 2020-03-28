package edu.marist.mscs710.metricscollector.data;

import edu.marist.mscs710.metricscollector.metric.Fields;
import edu.marist.mscs710.metricscollector.metric.Metric;
import edu.marist.mscs710.metricscollector.metric.MetricType;
import edu.marist.mscs710.metricscollector.system.Processes;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessData extends MetricData {
  private static final long BYTES_PER_KB = 1024L;
  private int pid;
  private String name;
  private long startTime; // Unix time millis
  private long upTime; // millis
  private double cpuUsage; // During previous delta millis
  private long memory; // Total bytes allocated to process and in RAM
  private long bytesRead; // Bytes read from disk during previous delta millis
  private long bytesWritten; // During previous delta millis
  private Processes.PidState pidState;

  public ProcessData(int pid, String name, long startTime, long upTime,
                     double cpuUsage, long memory, long bytesRead, long bytesWritten,
                     Processes.PidState pidState, long deltaMillis, long epochMillisTime) {
    this.pid = pid;
    this.name = name;
    this.startTime = startTime;
    this.upTime = upTime;
    this.cpuUsage = cpuUsage;
    this.memory = memory;
    this.bytesRead = bytesRead;
    this.bytesWritten = bytesWritten;
    this.deltaMillis = deltaMillis;
    this.pidState = pidState;
    this.epochMillisTime = epochMillisTime;
  }

  public ProcessData(int pid, String name, long epochMillisTime) {
    this(pid, name, -1, -1, -1, -1, -1, -1, Processes.PidState.ENDED, -1, epochMillisTime);
  }

  public int getPid() {
    return pid;
  }

  public String getName() {
    return name;
  }

  public long getStartTime() {
    return startTime;
  }

  public long getUpTime() {
    return upTime;
  }

  public double getCpuUsage() {
    return cpuUsage;
  }

  public long getMemory() {
    return memory;
  }

  public long getBytesRead() {
    return bytesRead;
  }

  public long getBytesWritten() {
    return bytesWritten;
  }

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
      ", bytesRead=" + bytesRead +
      ", bytesWritten=" + bytesWritten +
      ", pidState=" + pidState +
      ", deltaMillis=" + deltaMillis +
      ", epochMillisTime=" + epochMillisTime +
      '}';
  }

  private Map<String, Object> getProcessMap() {
    boolean isEndedState = pidState == Processes.PidState.ENDED;

    long kbMemory = isEndedState ? -1 : memory / BYTES_PER_KB;
    double kbRead = isEndedState ? -1 : ((double) bytesRead) / deltaMillis;
    double kbWritten = isEndedState ? -1 : ((double) bytesWritten) / deltaMillis;

    return new HashMap<String, Object>() {
      {
        put(Fields.Processes.DATETIME.toString(), epochMillisTime);
        put(Fields.Processes.DELTA_MILLIS.toString(), deltaMillis);
        put(Fields.Processes.PID.toString(), pid);
        put(Fields.Processes.NAME.toString(), name);
        put(Fields.Processes.START_TIME.toString(), startTime);
        put(Fields.Processes.UPTIME.toString(), upTime);
        put(Fields.Processes.CPU_USAGE.toString(), cpuUsage);
        put(Fields.Processes.MEMORY.toString(), kbMemory);
        put(Fields.Processes.KB_READ.toString(), kbRead);
        put(Fields.Processes.KB_WRITTEN.toString(), kbWritten);
        put(Fields.Processes.STATE.toString(), pidState.toString());
      }
    };
  }

  @Override
  public List<Metric> toMetricRecords() {
    return Collections.singletonList(new Metric(MetricType.PROCESSES, getProcessMap()));
  }
}
