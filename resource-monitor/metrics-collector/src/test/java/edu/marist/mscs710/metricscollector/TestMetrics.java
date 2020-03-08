package edu.marist.mscs710.metricscollector;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import oshi.SystemInfo;
import oshi.hardware.NetworkIF;

import java.util.Arrays;

public class TestMetrics {

  @Test
  public void testGetCpuCoreUsages() throws InterruptedException {
    Cpu cpu = new Cpu(true);

    Thread.sleep(300);

    double[] perCoreCpuUsages = cpu.getCpuCoreUsageSinceLastCheck();

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

      System.out.println(Arrays.toString(network.getNetworkStatsSinceLastCheck()));

      Thread.sleep(2000);
      for (NetworkIF n : nets)
        n.updateAttributes();
    }
  }
}
