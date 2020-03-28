package edu.marist.mscs710.metricscollector.system;

import edu.marist.mscs710.metricscollector.MetricSource;
import edu.marist.mscs710.metricscollector.data.SystemData;
import oshi.SystemInfo;
import oshi.software.os.OperatingSystem;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * Represents overall system metrics.
 */
public class SystemMetrics implements MetricSource {
  private OperatingSystem os;
  private long lastCheckInMillis;

  /**
   * Constructs a new <tt>SystemMetrics</tt>
   */
  public SystemMetrics() {
    os = new SystemInfo().getOperatingSystem();
    lastCheckInMillis = Instant.now().toEpochMilli();
  }

  @Override
  public List<SystemData> getMetricData() {
    long curMillis = Instant.now().toEpochMilli();
    long deltaMillis = curMillis - lastCheckInMillis;
    lastCheckInMillis = curMillis;

    return Collections.singletonList(
      new SystemData(getUpTime(), deltaMillis, curMillis)
    );
  }

  private long getUpTime() {
    return os.getSystemUptime();
  }
}
