package edu.marist.mscs710.metricscollector.system;

import edu.marist.mscs710.metricscollector.data.SystemData;
import oshi.SystemInfo;
import oshi.software.os.OperatingSystem;

import java.time.Instant;

public class SystemMetrics {
  private OperatingSystem os;
  private long lastCheckInMillis;

  public SystemMetrics() {
    os = new SystemInfo().getOperatingSystem();
    lastCheckInMillis = Instant.now().toEpochMilli();
  }

  public SystemData getSystemData() {
    long curMillis = Instant.now().toEpochMilli();
    long deltaMillis = curMillis - lastCheckInMillis;
    lastCheckInMillis = curMillis;

    return new SystemData(getUpTime(), deltaMillis, curMillis);
  }

  private long getUpTime() {
    return os.getSystemUptime();
  }
}
