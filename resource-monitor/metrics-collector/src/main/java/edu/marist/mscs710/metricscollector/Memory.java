package edu.marist.mscs710.metricscollector;

import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.hardware.VirtualMemory;

public class Memory {
  private static final double ONE_GB = 1073741824.0;
  private GlobalMemory mem;
  private VirtualMemory vMem;
  private final long TOTAL_MEM_BYTES;
  private final double TOTAL_MEM_GB;
  private long lastSwapPagesIn;

  public Memory() {
    this.mem = new SystemInfo().getHardware().getMemory();
    this.vMem = mem.getVirtualMemory();
    this.TOTAL_MEM_BYTES = mem.getTotal();
    this.TOTAL_MEM_GB = Math.round(10.0 * TOTAL_MEM_BYTES / ONE_GB) / 10.0;
    this.lastSwapPagesIn = vMem.getSwapPagesIn();
  }

  public double getMemoryUtilization() {
    return 1.0 - (double) mem.getAvailable() / TOTAL_MEM_BYTES; // 0.0-1.0
  }

  public long getPageFaultsSinceLastCheck() {
    long currentSwapPagesIn = vMem.getSwapPagesIn();
    long pageFaults = currentSwapPagesIn - lastSwapPagesIn;
    lastSwapPagesIn = currentSwapPagesIn;
    return pageFaults;
  }

  public long getTotalMemoryInBytes() {
    return TOTAL_MEM_BYTES;
  }

  public double getTotalMemoryInGb() {
    return TOTAL_MEM_GB;
  }
}
