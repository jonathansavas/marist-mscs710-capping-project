package edu.marist.mscs710.metricscollector;

/**
 * Provides a common interface to convert group <tt>Metric</tt> data and perform
 * various operations.
 */
public interface Metric {

  /**
   * Creates SQL INSERT statement for this <tt>Metric</tt> instance.
   *
   * @return sql insert String
   */
  String toSqlInsertString();
}
