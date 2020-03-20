package edu.marist.mscs710.metricscollector.system;

import edu.marist.mscs710.metricscollector.data.MemoryData;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.hardware.VirtualMemory;

import java.time.Instant;

public class Memory {
  private static final double ONE_GB = 1073741824.0;
  private GlobalMemory mem;
  private VirtualMemory vMem;
  private final long totalMemBytes;
  private final double totalMemGb;
  private long lastSwapPagesIn;
  private long lastCheckInMillis;

  public Memory() {
    this.mem = new SystemInfo().getHardware().getMemory();
    this.vMem = mem.getVirtualMemory();
    this.totalMemBytes = mem.getTotal();
    this.totalMemGb = Math.round(10.0 * totalMemBytes / ONE_GB) / 10.0;
    this.lastSwapPagesIn = vMem.getSwapPagesIn();
    this.lastCheckInMillis = Instant.now().toEpochMilli();
  }

  public MemoryData getMemoryData() {
    long pageFaults = getPageFaultsSinceLastCheck();

    long curMillis = Instant.now().toEpochMilli();
    long deltaMillis = curMillis - lastCheckInMillis;
    lastCheckInMillis = curMillis;

    return new MemoryData(getMemoryUtilization(), pageFaults, deltaMillis, curMillis);
  }

  public double getMemoryUtilization() {
    return 1.0 - (double) mem.getAvailable() / totalMemBytes; // 0.0-1.0
  }

  private long getPageFaultsSinceLastCheck() {
    long currentSwapPagesIn = vMem.getSwapPagesIn();
    long pageFaults = currentSwapPagesIn - lastSwapPagesIn;
    lastSwapPagesIn = currentSwapPagesIn;
    return pageFaults;
  }

  public long getTotalMemoryInBytes() {
    return totalMemBytes;
  }

  public double getTotalMemoryInGb() {
    return totalMemGb;
  }
}
