package edu.marist.mscs710.metricscollector;

import edu.marist.mscs710.metricscollector.metric.Metric;

import java.util.List;

public interface MetricRecord {

  List<? extends Metric> toMetricRecords();
}
