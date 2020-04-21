package edu.marist.mscs710.persistenceapi;

import edu.marist.mscs710.metricscollector.Metric;
import edu.marist.mscs710.metricscollector.data.MetricData;

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

  /**
   * Retrieve metrics from the database within the specified time interval. This
   *
   * @param earliest   epoch milli timestamp of the earliest record, inclusive
   * @param latest     epoch milli timestamp of the latest record, exclusive
   * @param clazz      class corresponding to <tt>metricType</tt> to hold the returned metric data
   * @param <T>        <tt>MetricData</tt> and its subtypes
   * @return list of metric data of type <tt>T</tt>
   */
  <T extends MetricData> List<T> getMetricsInRange(long earliest, long latest, Class<T> clazz);
}
