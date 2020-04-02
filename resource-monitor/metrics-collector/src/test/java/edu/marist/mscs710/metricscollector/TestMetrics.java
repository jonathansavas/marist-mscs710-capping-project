package edu.marist.mscs710.metricscollector;

import edu.marist.mscs710.metricscollector.data.MemoryData;
import edu.marist.mscs710.metricscollector.data.NetworkData;
import edu.marist.mscs710.metricscollector.data.ProcessData;
import edu.marist.mscs710.metricscollector.system.Cpu;
import edu.marist.mscs710.metricscollector.system.Memory;
import edu.marist.mscs710.metricscollector.system.Network;
import edu.marist.mscs710.metricscollector.system.Processes;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import oshi.SystemInfo;
import oshi.hardware.NetworkIF;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TestMetrics {

  @Test
  public void testGetCpuCoreUsages() throws InterruptedException {
    Cpu cpu = new Cpu(true);

    Thread.sleep(300);

    double[] perCoreCpuUsages = cpu.getMetricData().get(0).getCpuCoreUsages();

    int numPhysCores = new SystemInfo().getHardware().getProcessor().getPhysicalProcessorCount();

    Assert.assertEquals(numPhysCores, perCoreCpuUsages.length);

    for (double pct : perCoreCpuUsages) {
      Assert.assertTrue(pct >= 0 && pct <= 1);
    }
  }

  @Test
  public void testMemoryData() throws InterruptedException {
    Memory memory = new Memory();

    Thread.sleep(500);

    MemoryData memData = memory.getMetricData().get(0);

    Assert.assertTrue(memData.getMemoryUtilization() > 0 && memData.getMemoryUtilization() < 1.0);
    Assert.assertTrue(memData.getPageFaults() >= 0);
    Assert.assertTrue(memData.getDeltaMillis() > 0);

    Thread.sleep(500);

    MemoryData newData = memory.getMetricData().get(0);

    Assert.assertTrue(newData.getDeltaMillis() > 0);
    Assert.assertTrue(newData.getEpochMillisTime() > memData.getEpochMillisTime());
    Assert.assertTrue(newData.getPageFaults() >= 0);
    Assert.assertTrue(newData.getMemoryUtilization() > 0 && newData.getMemoryUtilization() < 1.0);
  }

  @Test
  @Ignore
  public void viewNetwork() throws InterruptedException {
    Network network = new Network();
    NetworkIF[] nets = new SystemInfo().getHardware().getNetworkIFs();

    for (int i = 0; i < 5; i++) {
      System.out.println();

      for (NetworkIF n : nets) {
        System.out.println(n.getDisplayName() + ": " + n.getBytesRecv() + " bytes rec, " + n.getBytesSent() + " bytes sent, " + n.getSpeed() + " speed");
      }

      System.out.println();

      NetworkData netData = network.getMetricData().get(0);

      System.out.println(String.format("Bytes Sent: %s, bytes recv: %s, bit / s: %s, delta millis: %s",
        netData.getBytesSent(), netData.getBytesRecv(), netData.getSpeed(), netData.getDeltaMillis()));

      Thread.sleep(2000);
      for (NetworkIF n : nets)
        n.updateAttributes();
    }
  }

  @Test
  @Ignore
  public void viewProcesses() throws InterruptedException {
    SystemInfo sys = new SystemInfo();
    OperatingSystem os = sys.getOperatingSystem();

    OSProcess[] processes;

    System.out.println(String.format("Current seconds: %s, Millis: %s", Instant.now().getEpochSecond(), Instant.now().toEpochMilli()));
    System.out.println();

    int cpus = sys.getHardware().getProcessor().getLogicalProcessorCount(); // We want to do this on Windows, haven't tested whether this is correct
                                                                            // for Linux.

    for (int i = 0; i < 3; i++) {

      processes = os.getProcesses(10, OperatingSystem.ProcessSort.PID);

      for (OSProcess p : processes) {
        System.out.println(String.format("PID: %s, CPU: %s, Uptime: %s, Start: %s, Name: %s, Memory: %s",
          p.getProcessID(), p.calculateCpuPercent() / cpus, p.getUpTime(), p.getStartTime(), p.getName(), p.getResidentSetSize()));
      }

      System.out.println();

      Thread.sleep(2000);
    }
  }

  @Test
  @Ignore
  public void viewGetProcesses() throws InterruptedException {
    Processes processes = new Processes();

    for (int j = 0; j < 2; j++) {

      Thread.sleep(2500);
      System.out.println();
      System.out.println("EpochMillis: " + Instant.now().toEpochMilli());
      System.out.println();

      List<ProcessData> processDataList = processes.getMetricData();

      System.out.println(processDataList.size());

      for (int i = 0; i < 10; i++) {
        System.out.println(processDataList.get(i).toString());
      }
      System.out.println(processDataList.get(processDataList.size() - 1).toString());
      System.out.println();
    }
  }

  @Test
  @Ignore
  public void testProcessCpuUsageTotal() throws InterruptedException {
    Processes processes = new Processes();
    Thread.sleep(3000);

    List<ProcessData> pdl = processes.getMetricData();

    Assert.assertEquals(1.0, pdl.stream().mapToDouble(ProcessData::getCpuUsage).sum(), 0.05);
  }

  @Test
  public void testGetProcesses() throws InterruptedException {
    Processes processes = new Processes();
    Thread.sleep(1000);

    List<ProcessData> pdl = processes.getMetricData();

    Map<Integer, ProcessData> priorProcs = pdl.stream().filter(Objects::nonNull)
      .collect(Collectors.toMap(ProcessData::getPid, Function.identity()));

    Thread.sleep(2000);

    pdl = processes.getMetricData();

    for (ProcessData pd : pdl) {
      Processes.PidState state = pd.getPidState();
      ProcessData prior = priorProcs.get(pd.getPid());

      switch (state) {
        case RUNNING:
          Assert.assertNotNull(prior);
          Assert.assertEquals(pd.getStartTime(), prior.getStartTime());
          Assert.assertTrue(pd.getUpTime() > prior.getUpTime());
          break;
        case RECYCLED:
          Assert.assertNotNull(prior);
          Assert.assertTrue(pd.getStartTime() > prior.getStartTime());
          break;
        case NEW:
          Assert.assertNull(prior);
          break;
        case ENDED:
          Assert.assertNotNull(prior);
          break;
      }
    }
  }
}
