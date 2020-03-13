package edu.marist.mscs710.metricscollector;

import edu.marist.mscs710.metricscollector.data.NetworkData;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import oshi.SystemInfo;
import oshi.hardware.NetworkIF;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class TestMetrics {

  @Test
  public void testGetCpuCoreUsages() throws InterruptedException {
    Cpu cpu = new Cpu(true);

    Thread.sleep(300);

    double[] perCoreCpuUsages = cpu.getCpuData().getCpuCoreUsages();

    int numPhysCores = new SystemInfo().getHardware().getProcessor().getPhysicalProcessorCount();

    Assert.assertEquals(numPhysCores, perCoreCpuUsages.length);

    for (double pct : perCoreCpuUsages) {
      Assert.assertTrue(pct >= 0 && pct <= 1);
    }
  }

  @Test
  @Ignore
  public void network() throws InterruptedException {
    Network network = new Network();
    NetworkIF[] nets = new SystemInfo().getHardware().getNetworkIFs();

    for (int i = 0; i < 5; i++) {

      for (NetworkIF n : nets) {
        System.out.println(n.getDisplayName() + ": " + n.getBytesRecv() + " bytes rec, " + n.getBytesSent() + " bytes sent, " + n.getSpeed() + " speed");
      }

      System.out.println();

      NetworkData netData = network.getNetworkStatsSinceLastCheck();

      System.out.println(String.format("Bytes Sent: %s, bytes recv: %s, bit / s: %s, delta millis: %s",
        netData.getBytesSent(), netData.getBytesRecv(), netData.getSpeed(), netData.getDeltaMillis()));

      Thread.sleep(2000);
      for (NetworkIF n : nets)
        n.updateAttributes();
    }
  }

  @Test
  @Ignore
  public void testProcesses() throws InterruptedException {
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
  public void testConversion() {
    long a = 1584123074465L;
    long b = 1584123074464L;

    System.out.println(Math.round(a / 100.0) * 100);
    System.out.println(Math.round(b / 100.0) * 100);

  }
}
