package edu.marist.mscs710.metricscollector.utils;

public class DataUtils {

  public static long weightedAverage(long agg, long wAgg, long val, double wVal) {
    return (long) (agg + (val - agg) * (wVal / (wVal + wAgg)));
  }

  public static double weightedAverage(double agg, long wAgg, double val, double wVal) {
    return agg + (val - agg) * ( wVal / (wVal + wAgg));
  }
}
