package edu.marist.mscs710.metricscollector.data;

import edu.marist.mscs710.metricscollector.system.Processes;

public class ProcessData extends MetricData {
  private int pid;
  private String name;
  private long startTime; // Unix time millis
  private long upTime; // millis
  private double cpuUsage;
  private long memory; // bytes allocated to process and in RAM
  private long bytesRead; // During delta millis
  private long bytesWritten; // During delta millis
  private Processes.PidState pidState;

  public ProcessData(int pid, String name, long startTime, long upTime,
                     double cpuUsage, long memory, long bytesRead, long bytesWritten,
                     Processes.PidState pidState, long deltaMillis) {
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
  }

  public ProcessData(int pid) {
    this(pid, "", -1, -1, -1, -1, -1, -1, Processes.PidState.ENDED, -1);
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
}
