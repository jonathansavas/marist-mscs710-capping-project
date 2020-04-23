package edu.marist.mscs710.metricscollector.utils;

public class DataUtils {
  /**
   * Calculates the weighted average of two values.
   *
   * @param agg  aggregated value
   * @param wAgg weight of <tt>agg</tt> relative to <tt>val</tt>
   * @param val  new value
   * @param wVal weight of <tt>val</tt> relative to <tt>agg</tt>
   * @return weighted average of <tt>agg</tt> and <tt>val</tt>
   */
  public static double weightedAverage(double agg, long wAgg, double val, double wVal) {
    return agg + (val - agg) * (wVal / (wVal + wAgg));
  }
}
