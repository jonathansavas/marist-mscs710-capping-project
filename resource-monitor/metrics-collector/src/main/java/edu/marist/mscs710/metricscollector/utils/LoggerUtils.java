package edu.marist.mscs710.metricscollector.utils;

import java.util.Arrays;

public class LoggerUtils {

  public static String getExceptionMessage(Exception ex) {
    return ex.getMessage() + System.lineSeparator() + Arrays.toString(ex.getStackTrace());
  }
}
