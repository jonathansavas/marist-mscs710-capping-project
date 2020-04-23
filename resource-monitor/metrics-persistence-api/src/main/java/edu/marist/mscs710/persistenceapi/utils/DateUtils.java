package edu.marist.mscs710.persistenceapi.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
  private static final String MS_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss,SSS";

  /**
   * Converts an epoch milli timestamp to date string in format "yyyy-MM-dd HH:mm:ss,SSS".
   *
   * @param epochMillis epoch milli timestamp
   * @return date String
   */
  public static String convertEpochMillisDateFormat(long epochMillis) {
    return new SimpleDateFormat(MS_DATE_FORMAT).format(new Date(epochMillis));
  }
}
