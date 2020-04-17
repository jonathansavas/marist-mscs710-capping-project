package edu.marist.mscs710.metricscollector.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import edu.marist.mscs710.metricscollector.Metric;
import edu.marist.mscs710.metricscollector.metric.Fields;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Abstract base class for all other <tt>MetricData</tt> objects.
 */
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  property = Fields.METRIC_TYPE
)
@JsonSubTypes({
  @JsonSubTypes.Type(value = CpuData.class, name = Fields.METRIC_TYPE_CPU),
  @JsonSubTypes.Type(value = CpuCoreData.class, name = Fields.METRIC_TYPE_CPU_CORE),
  @JsonSubTypes.Type(value = MemoryData.class, name = Fields.METRIC_TYPE_MEMORY),
  @JsonSubTypes.Type(value = NetworkData.class, name = Fields.METRIC_TYPE_NETWORK),
  @JsonSubTypes.Type(value = ProcessData.class, name = Fields.METRIC_TYPE_PROCESSES),
  @JsonSubTypes.Type(value = SystemData.class, name = Fields.METRIC_TYPE_SYSTEM_METRICS),
})
public abstract class MetricData implements Metric {

  @JsonProperty(Fields.DELTA_MILLIS)
  protected long deltaMillis;

  @JsonProperty(Fields.DATETIME)
  protected long epochMillisTime;

  /**
   * Gets time in milliseconds covered by this snapshot.
   *
   * @return number of milliseconds
   */
  public long getDeltaMillis() {
    return deltaMillis;
  }

  /**
   * Gets the time epoch milli timestamp of this snapshot.
   *
   * @return epoch milli timestamp
   */
  public long getEpochMillisTime() {
    return epochMillisTime;
  }

  /**
   * Sorts a list of <tt>MetricData</tt> chronologically by <tt>epochMillisTime</tt>,
   * from earliest to latest. This method does not modify the order of the original
   * list, but manipulating data in the returned list will do the same to the
   * original list.
   *
   * @param metrics list of metrics to sort
   * @param <T>     <tt>MetricData</tt> or its subtypes
   * @return sorted list of metric data
   */
  public static <T extends MetricData> List<T> sortChronologically(List<T> metrics) {
    return metrics.stream()
      .sorted(Comparator.comparing(MetricData::getEpochMillisTime))
      .collect(Collectors.toList());
  }
}
