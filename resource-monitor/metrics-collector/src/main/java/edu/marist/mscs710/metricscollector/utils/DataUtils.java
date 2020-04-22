package edu.marist.mscs710.metricscollector.utils;

public class DataUtils {
  public static double weightedAverage(double agg, long wAgg, double val, double wVal) {
    return agg + (val - agg) * (wVal / (wVal + wAgg));
  }
}
