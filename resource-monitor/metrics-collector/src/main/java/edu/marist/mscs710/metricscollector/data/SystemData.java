package edu.marist.mscs710.metricscollector.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.marist.mscs710.metricscollector.metric.Fields;

import java.util.List;

import static edu.marist.mscs710.metricscollector.utils.DataUtils.weightedAverage;

/**
 * Holds a snapshot of System data.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SystemData extends MetricData {
  public static final String SQL_INSERT_PREFIX = "INSERT INTO " +
    Fields.METRIC_TYPE_SYSTEM_METRICS + " (" +
    Fields.SYSTEM_METRICS_DATETIME + ',' +
    Fields.SYSTEM_METRICS_DELTA_MILLIS + ',' +
    Fields.SYSTEM_METRICS_UPTIME + ") VALUES ";

  @JsonProperty(Fields.SYSTEM_METRICS_UPTIME)
  private long upTime; // Seconds since system boot

  /**
   * Constructs a new <tt>SystemData</tt> with the supplied metrics.
   *
   * @param upTime          number of seconds since system boot
   * @param deltaMillis     time covered by this snapshot
   * @param epochMillisTime epoch milli timestamp of this snapshot
   */
  @JsonCreator
  public SystemData(@JsonProperty(Fields.SYSTEM_METRICS_UPTIME)long upTime,
                    @JsonProperty(Fields.DELTA_MILLIS)long deltaMillis,
                    @JsonProperty(Fields.DATETIME) long epochMillisTime) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SystemData that = (SystemData) o;
    return upTime == that.upTime&&
      deltaMillis == that.deltaMillis &&
      epochMillisTime == that.epochMillisTime;
  }

  @Override
  public String toSqlInsertString() {
    return SQL_INSERT_PREFIX + '(' +
      epochMillisTime + ',' +
      deltaMillis + ',' +
      upTime + ')' + ';';
  }

  public static SystemData combine(List<SystemData> metrics) {
    long datetime = 0;
    long totalMillis = 0;
    long upTime = 0;

    for (SystemData data : metrics) {
      long deltaMillis = data.getDeltaMillis();
      datetime = weightedAverage(datetime, totalMillis, data.getEpochMillisTime(), deltaMillis);
      upTime = weightedAverage(upTime, totalMillis, data.getUpTime(), deltaMillis);
      totalMillis += deltaMillis;
    }

    return new SystemData(upTime, totalMillis, datetime);
  }
}
