package edu.marist.mscs710.metricscollector;

import edu.marist.mscs710.metricscollector.data.MetricData;

import java.util.List;

public interface MetricSource {

  List<? extends MetricData> getMetricData();
}
