package edu.marist.mscs710.metricscollector;

import edu.marist.mscs710.metricscollector.data.MetricData;

import java.util.List;

/**
 * Provides a common interface to extract metric data from various sources
 * in a system.
 */
public interface MetricSource {

  /**
   * Gets metric data from a system. The data returned is a snapshot of data
   * aggregated since the last time this method was called.
   * @return list of <tt>MetricData</tt> objects
   */
  List<? extends MetricData> getMetricData();
}
