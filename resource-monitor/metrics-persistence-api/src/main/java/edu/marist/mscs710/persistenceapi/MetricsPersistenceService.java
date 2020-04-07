package edu.marist.mscs710.persistenceapi;

import edu.marist.mscs710.metricscollector.metric.Metric;

import java.util.List;

public interface MetricsPersistenceService {

  boolean persistMetric(Metric metric);

  List<String> getMetricTypes();

}
