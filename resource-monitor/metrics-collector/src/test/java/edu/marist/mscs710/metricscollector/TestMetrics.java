package edu.marist.mscs710.metricscollector;

import org.junit.Assert;
import org.junit.Test;
import oshi.SystemInfo;

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
}
