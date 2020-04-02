package edu.marist.mscs710.metricscollector;

import edu.marist.mscs710.metricscollector.metric.Metric;

import java.util.List;

/**
 * Provides a common interface to convert metric data associated with a certain
 * type to generic <tt>Metric</tt> objects. This is useful for serializing
 * data in a generic format for use by various sources.
 */
public interface MetricRecord {

  /**
   * Extracts metric data in a generic format.
   *
   * @return list of generic <tt>Metric</tt> objects
   */
  List<? extends Metric> toMetricRecords();
}
