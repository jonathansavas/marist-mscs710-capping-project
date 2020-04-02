package edu.marist.mscs710.metricscollector.data;

import edu.marist.mscs710.metricscollector.metric.Fields;
import edu.marist.mscs710.metricscollector.metric.Metric;
import edu.marist.mscs710.metricscollector.metric.MetricType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds a snapshot of System data.
 */
public class SystemData extends MetricData {
  private long upTime; // Seconds since system boot

  /**
   * Constructs a new <tt>SystemData</tt> with the supplied metrics.
   *
   * @param upTime          number of seconds since system boot
   * @param deltaMillis     time covered by this snapshot
   * @param epochMillisTime epoch milli timestamp of this snapshot
   */
  public SystemData(long upTime, long deltaMillis, long epochMillisTime) {
    this.upTime = upTime;
    this.deltaMillis = deltaMillis;
    this.epochMillisTime = epochMillisTime;
  }

  /**
   * Gets the uptime of the system.
   *
   * @return seconds since boot time
   */
  public long getUpTime() {
    return upTime;
  }

  @Override
  public String toString() {
    return "SystemData{" +
      "upTime=" + upTime +
      ", deltaMillis=" + deltaMillis +
      ", epochMillisTime=" + epochMillisTime +
      '}';
  }

  private Map<String, Object> getSystemDataMap() {
    return new HashMap<String, Object>() {
      {
        put(Fields.SystemMetrics.DATETIME.toString(), epochMillisTime);
        put(Fields.SystemMetrics.UPTIME.toString(), upTime);
      }
    };
  }

  @Override
  public List<Metric> toMetricRecords() {
    return Collections.singletonList(new Metric(MetricType.SYSTEM_METRICS, getSystemDataMap()));
  }
}
