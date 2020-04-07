package edu.marist.mscs710.persistenceapi;

import edu.marist.mscs710.metricscollector.metric.Metric;

import java.util.List;

/**
 * Provides a common interface for interacting with various database
 * implementations for persisting <tt>Metric</tt> data.
 */
public interface MetricsPersistenceService {

  /**
   * Inserts the supplied <tt>Metric</tt> into the database.
   *
   * @param metric a <tt>Metric</tt> object
   * @return true if the operation is successful, false otherwise
   */
  boolean persistMetric(Metric metric);

  /**
   * Gets the metric types available in the database
   *
   * @return list of metric types
   */
  List<String> getMetricTypes();
}
