package edu.marist.mscs710.metricscollector.system;

import edu.marist.mscs710.metricscollector.data.ProcessData;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.util.*;
import java.util.stream.Collectors;

public class Processes {
  private final int numLogicalCores;
  private OperatingSystem os;
  private Map<Integer, ProcessInfo> lastProcInfoCache;

  private static class ProcessInfo {
    int pid;
    long startTime;
    long upTime;
    long bytesRead;
    long bytesWritten;

    public ProcessInfo(int pid, long startTime, long upTime, long bytesRead, long bytesWritten) {
      this.pid = pid;
      this.startTime = startTime;
      this.upTime = upTime;
      this.bytesRead = bytesRead;
      this.bytesWritten = bytesWritten;
    }
  }

  public enum PidState {
    NEW, // Pid is new since last update
    RUNNING, // Pid is same process as last update
    RECYCLED, // Pid is a different process with the same pid as one in the last update, special case of NEW
    ENDED // Pid has ended since the last update
  }

  public Processes() {
    SystemInfo sys = new SystemInfo();
    numLogicalCores = sys.getHardware().getProcessor().getLogicalProcessorCount();
    os = sys.getOperatingSystem();

    updateProcessCache(getCurrentProcesses());
  }

  public List<ProcessData> getProcessData() {
    List<OSProcess> osProcesses = getCurrentProcesses();
    List<ProcessData> dataList = new ArrayList<>(osProcesses.size());

    for (OSProcess p : osProcesses) {
      ProcessData pData = createProcessDataAndDeleteFromCache(p);

      if (pData != null)
        dataList.add(pData);
    }

    for (Integer endedPid : lastProcInfoCache.keySet()) {
      dataList.add(new ProcessData(endedPid));
    }

    updateProcessCache(osProcesses);

    return dataList;
  }

  private ProcessData createProcessDataAndDeleteFromCache(OSProcess p) {
    if (p == null) return null;

    int pid = p.getProcessID();
    ProcessInfo lastInfo = lastProcInfoCache.remove(pid);

    if (lastInfo != null) { // Recycled pid or already-running process
      boolean recycled = isRecycledPid(lastInfo, p);

      if (!recycled)
        p.setStartTime(lastInfo.startTime); // start time has +- 1 ms error between process updates, so update
                                            // this to always return original start time, this is RUNNING case
      return new ProcessData(
        pid,
        p.getName(),
        recycled ? p.getStartTime() : lastInfo.startTime,
        p.getUpTime(),
        p.calculateCpuPercent() / numLogicalCores,
        p.getResidentSetSize(),
        recycled ? p.getBytesRead() : p.getBytesRead() - lastInfo.bytesRead,
        recycled ? p.getBytesWritten() : p.getBytesWritten() - lastInfo.bytesWritten,
        recycled ? PidState.RECYCLED : PidState.RUNNING,
        recycled ? p.getUpTime() : p.getUpTime() - lastInfo.upTime
      );
    } else { // New process
      return new ProcessData(pid,
        p.getName(),
        p.getStartTime(),
        p.getUpTime(),
        p.calculateCpuPercent() / numLogicalCores,
        p.getResidentSetSize(),
        p.getBytesRead(),
        p.getBytesWritten(),
        PidState.NEW,
        p.getUpTime()
      );
    }
  }

  private boolean isRecycledPid(ProcessInfo lastInfo, OSProcess p) {
    return lastInfo.pid == p.getProcessID() && p.getStartTime() > lastInfo.startTime + 5;
  }

  private List<OSProcess> getCurrentProcesses() {
    OSProcess[] osProcesses = os.getProcesses();
    if (osProcesses == null) return new ArrayList<>();

    List<Integer> pids = new ArrayList<>(osProcesses.length);

    for (OSProcess p : osProcesses) {
      if (p != null)
        pids.add(p.getProcessID());
    }

    List<OSProcess> processList = os.getProcesses(pids);
    while (processList.size() < pids.size()) {
      pids.clear();

      for (OSProcess p : processList) {
        if (p != null)
          pids.add(p.getProcessID());
      }

      processList = os.getProcesses(pids);
    }

    return processList;
  }

  private void updateProcessCache(List<OSProcess> processes) {
    lastProcInfoCache = processes.stream()
      .filter(Objects::nonNull)
      .collect(Collectors.toMap(OSProcess::getProcessID,
        p -> new ProcessInfo(p.getProcessID(),
          p.getStartTime(),
          p.getUpTime(),
          p.getBytesRead(),
          p.getBytesWritten())));
  }
}
