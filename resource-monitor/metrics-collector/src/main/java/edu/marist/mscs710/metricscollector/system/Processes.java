package edu.marist.mscs710.metricscollector.system;

import edu.marist.mscs710.metricscollector.MetricSource;
import edu.marist.mscs710.metricscollector.data.ProcessData;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents the processes running on an operating system. Produces metrics
 * on demand, keeping the previous state of the processes.
 */
public class Processes implements MetricSource {
  private final int numLogicalCores;
  private OperatingSystem os;
  private Map<Integer, OSProcess> priorProcSnapshots;

  /**
   * State of a process
   */
  public enum PidState {
    /**
     * A process that has started since the last state update
     */
    NEW,
    /**
     * A process that was running during the last state update, and is
     * currently running
     */
    RUNNING,
    /**
     * A process that has started since the last state update, but has the
     * same process ID as a process that was seen during the last state update
     */
    RECYCLED,
    /**
     * A process that was running during the last state update, but has ended
     * since then
     */
    ENDED
  }

  /**
   * Constructs a new <tt>Processes</tt>
   */
  public Processes() {
    SystemInfo sys = new SystemInfo();
    numLogicalCores = sys.getHardware().getProcessor().getLogicalProcessorCount();
    os = sys.getOperatingSystem();

    updateProcessSnapshots(getCurrentProcesses());
  }

  @Override
  public List<ProcessData> getMetricData() {
    List<OSProcess> osProcesses = getCurrentProcesses();
    List<ProcessData> dataList = new ArrayList<>(osProcesses.size());

    for (OSProcess p : osProcesses) {
      ProcessData pData = createProcessDataAndDeleteFromCache(p);

      if (pData != null)
        dataList.add(pData);
    }

    for (OSProcess endedProc : priorProcSnapshots.values()) {
      dataList.add(new ProcessData(endedProc.getProcessID(),
                                   endedProc.getName(),
                                   Instant.now().toEpochMilli()));
    }

    updateProcessSnapshots(osProcesses);

    return dataList;
  }

  private ProcessData createProcessDataAndDeleteFromCache(OSProcess p) {
    if (p == null) return null;

    int pid = p.getProcessID();
    OSProcess prior = priorProcSnapshots.remove(pid);

    if (prior != null) { // Recycled pid or already-running process
      boolean recycled = isRecycledPid(prior, p);

      if (!recycled)
        p.setStartTime(prior.getStartTime()); // start time has +- 1 ms error between process updates, so update
                                            // this to always return original start time, this is RUNNING case
      return new ProcessData(
        pid,
        p.getName(),
        p.getStartTime(), // If not recycled, we have updated start time above, else start time of new (recycled) process
        p.getUpTime(),
        getProcessCpuLoadBetweenChecks(prior, p) / numLogicalCores,
        p.getResidentSetSize(),
        recycled ? p.getBytesRead() : p.getBytesRead() - prior.getBytesRead(),
        recycled ? p.getBytesWritten() : p.getBytesWritten() - prior.getBytesWritten(),
        recycled ? PidState.RECYCLED : PidState.RUNNING,
        recycled ? p.getUpTime() : p.getUpTime() - prior.getUpTime(),
        p.getStartTime() + p.getUpTime()
      );
    } else { // New process
      return new ProcessData(pid,
        p.getName(),
        p.getStartTime(),
        p.getUpTime(),
        getTotalCpuLoad(p) / numLogicalCores,
        p.getResidentSetSize(),
        p.getBytesRead(),
        p.getBytesWritten(),
        PidState.NEW,
        p.getUpTime(),
        p.getStartTime() + p.getUpTime()
      );
    }
  }

  private static boolean isRecycledPid(OSProcess prior, OSProcess p) {
    return prior != null
      && p != null
      && prior.getProcessID() == p.getProcessID()
      && p.getStartTime() > prior.getStartTime() + 5;
  }

  private static double getProcessCpuLoadBetweenChecks(OSProcess prior, OSProcess p) {
    if (isRecycledPid(prior, p)) {
      return getTotalCpuLoad(p);
    } else {
      return (p.getUserTime() - prior.getUserTime() + p.getKernelTime() - prior.getKernelTime())
              / (double) (p.getUpTime() - prior.getUpTime());
    }
  }

  private static double getTotalCpuLoad(OSProcess p) {
    return p == null ? -1 : (p.getKernelTime() + p.getUserTime()) / (double) p.getUpTime();
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

  private void updateProcessSnapshots(List<OSProcess> processes) {
    priorProcSnapshots = processes.stream()
      .filter(Objects::nonNull)
      .collect(Collectors.toMap(OSProcess::getProcessID, Function.identity()));
  }
}
