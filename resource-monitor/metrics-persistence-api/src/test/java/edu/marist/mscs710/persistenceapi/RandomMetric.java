package edu.marist.mscs710.persistenceapi;

import edu.marist.mscs710.metricscollector.data.*;
import edu.marist.mscs710.metricscollector.system.Processes;
import edu.marist.mscs710.metricscollector.system.SystemConstants;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class RandomMetric {
  private static final int MAX_INT = 0x7ffffffe;
  private static final long MAX_LONG = 0x7ffffffffffffffeL;
  private static final double MAX_DOUBLE = MAX_INT;
  
  public static SystemConstants getRandomSystemConstants() {
    return new SystemConstants(ranDouble(), ranInt(), ranInt(), ranDouble());
  }
  
  public static NetworkData getRandomNetworkData() {
    return new NetworkData(ranDouble(), ranDouble(), ranLong(), ranLong(), ranLong());
  }

  public static MemoryData getRandomMemoryData() {
    return new MemoryData(ranDouble(), ranDouble(), ranLong(), ranLong());
  }

  public static CpuCoreData getRandomCpuCoreData() {
    return new CpuCoreData(ranInt(), ranDouble(), ranLong(), ranLong());
  }

  public static CpuData getRandomCpuData() {
    return new CpuData(ranDouble(), ranDouble(), ranLong(), ranLong());
  }

  public static ProcessData getRandomProcessData() {
    return new ProcessData(ranInt(), UUID.randomUUID().toString(), ranLong(), ranLong(), ranDouble(),
      ranLong(), ranDouble(), ranDouble(), Processes.PidState.RUNNING, ranLong(), ranLong());
  }

  public static SystemData getRandomSystemData() {
    return new SystemData(ranLong(), ranLong(), ranLong());
  }

  public static long ranLong() {
    return ThreadLocalRandom.current().nextLong(MAX_LONG);
  }

  public static int ranInt() {
    return ThreadLocalRandom.current().nextInt(MAX_INT);
  }

  public static double ranDouble() {
    return ThreadLocalRandom.current().nextDouble(MAX_DOUBLE);
  }
}
