package edu.marist.mscs710.metricscollector;

import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.util.*;
import java.util.stream.Collectors;

public class Processes {
  private final int numLogicalCores;
  private OperatingSystem os;
  private Map<Integer, ProcessInfo> lastProcesses;

  public Processes() {
    SystemInfo sys = new SystemInfo();
    numLogicalCores = sys.getHardware().getProcessor().getLogicalProcessorCount();
    os = sys.getOperatingSystem();

    updateProcessCache(getCurrentProcesses());
  }

  // Check for reused PID if startTime > old start time + 5, add boolean for this in ProcessData
  // Can also add field for pid that has died (if exists in cache but not in getCUrrentProcesses, Maybe delete map entry when you check against current list,
  // then iterate remaining entries, find dead pids, then updateProcessCache

  private List<OSProcess> getCurrentProcesses() {
    OSProcess[] processes = os.getProcesses();
    if (processes == null) return new ArrayList<>();

    List<Integer> pids = new ArrayList<>();

    for (OSProcess p : processes) {
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
    lastProcesses = processes.stream()
      .filter(Objects::nonNull)
      .collect(Collectors.toMap(OSProcess::getProcessID,
        p -> new ProcessInfo(p.getProcessID(),
                             p.getStartTime(),
                             p.getUpTime(),
                             p.getBytesRead(),
                             p.getBytesWritten())));
  }
}

class ProcessInfo {
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
