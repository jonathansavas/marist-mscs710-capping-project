package edu.marist.mscs710.metricscollector.system;

import edu.marist.mscs710.metricscollector.MetricSource;
import edu.marist.mscs710.metricscollector.data.MemoryData;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.hardware.VirtualMemory;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * Represents the memory unit of an operating system. Produces metrics on demand,
 * keeping the previous state of the memory.
 */
public class Memory implements MetricSource {
  private static final double ONE_GB = 1073741824.0;
  private GlobalMemory mem;
  private VirtualMemory vMem;
  private final long totalMemBytes;
  private final double totalMemGb;
  private long lastSwapPagesIn;
  private long lastCheckInMillis;

  /**
   * Constructs a new <tt>Memory</tt>
   */
  public Memory() {
    this.mem = new SystemInfo().getHardware().getMemory();
    this.vMem = mem.getVirtualMemory();
    this.totalMemBytes = mem.getTotal();
    this.totalMemGb = Math.round(10.0 * totalMemBytes / ONE_GB) / 10.0;
    this.lastSwapPagesIn = vMem.getSwapPagesIn();
    this.lastCheckInMillis = Instant.now().toEpochMilli();
  }

  @Override
  public List<MemoryData> getMetricData() {
    long pageFaults = getPageFaultsSinceLastCheck();

    long curMillis = Instant.now().toEpochMilli();
    long deltaMillis = curMillis - lastCheckInMillis;
    lastCheckInMillis = curMillis;

    return Collections.singletonList(
      new MemoryData(getMemoryUtilization(), pageFaults, deltaMillis, curMillis)
    );
  }

  /**
   * Gets the percent memory utilization, from 0.0-1.0
   * @return memory utilization
   */
  public double getMemoryUtilization() {
    return 1.0 - (double) mem.getAvailable() / totalMemBytes; // 0.0-1.0
  }

  private long getPageFaultsSinceLastCheck() {
    long currentSwapPagesIn = vMem.getSwapPagesIn();
    long pageFaults = currentSwapPagesIn - lastSwapPagesIn;
    lastSwapPagesIn = currentSwapPagesIn;
    return pageFaults;
  }

  /**
   * Gets the total memory of the system in bytes.
   * @return total memory
   */
  public long getTotalMemoryInBytes() {
    return totalMemBytes;
  }

  /**
   * Gets the total memory of the system in GB.
   * @return total memory
   */
  public double getTotalMemoryInGb() {
    return totalMemGb;
  }
}
