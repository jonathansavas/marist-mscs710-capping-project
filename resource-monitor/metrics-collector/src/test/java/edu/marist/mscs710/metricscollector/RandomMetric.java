package edu.marist.mscs710.metricscollector;

import edu.marist.mscs710.metricscollector.data.*;
import edu.marist.mscs710.metricscollector.system.Processes;
import edu.marist.mscs710.metricscollector.system.SystemConstants;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class RandomMetric {
  
  public static SystemConstants getRandomSystemConstants() {
    return new SystemConstants(
      ranDouble(64),
      ranInt(4),
      ranInt(8),
      ranDouble(10));
  }
  
  public static NetworkData getRandomNetworkData() {
    return new NetworkData(
      ranDouble(1e9),
      ranDouble(1e9),
      ranLong(0, (long) 1e9),
      ranLong(5000, 1000 * 60),
      Instant.now().toEpochMilli());
  }

  public static MemoryData getRandomMemoryData() {
    return new MemoryData(
      ranDouble(1.0),
      ranDouble(1E9),
      ranLong(5000, 1000 * 60),
      Instant.now().toEpochMilli());
  }

  public static CpuCoreData getRandomCpuCoreData() {
    return new CpuCoreData(
      ranInt(4),
      ranDouble(1.0),
      ranLong(5000, 1000 * 60),
      Instant.now().toEpochMilli());
  }

  public static CpuData getRandomCpuData() {
    return new CpuData(
      ranDouble(1.0),
      ranDouble(100.0),
      ranLong(5000, 1000 * 60),
      Instant.now().toEpochMilli());
  }

  public static ProcessData getRandomProcessData() {
    return new ProcessData(
      ranInt(100),
      UUID.randomUUID().toString(),
      ranLong(Instant.now().toEpochMilli() - 1000 * 60 * 60 * 24, Instant.now().toEpochMilli()),
      ranLong(0, 1000 * 60 * 60 * 24), ranDouble(1.0),
      ranLong(0, 10000000),
      ranDouble(1000000),
      ranDouble(1000000),
      Processes.PidState.RUNNING,
      ranLong(5000, 1000 * 60),
      Instant.now().toEpochMilli());
  }

  public static SystemData getRandomSystemData() {
    return new SystemData(
      ranLong(0, (long) 3e7),
      ranLong(5000, 1000 * 60),
      Instant.now().toEpochMilli());
  }

  public static long ranLong(long min, long max) {
    return ThreadLocalRandom.current().nextLong(min, max);
  }

  public static int ranInt(int max) {
    return ThreadLocalRandom.current().nextInt(max);
  }

  public static double ranDouble(double max) {
    return ThreadLocalRandom.current().nextDouble(max);
  }
}
