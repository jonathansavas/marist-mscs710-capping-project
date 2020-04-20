package edu.marist.mscs710.metricscollector;

import edu.marist.mscs710.metricscollector.data.CpuData;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CombineMetricsTest {

  @Test
  public void testCombineCpu() {
    for (int i = 0; i < 100; i++) {
      combineCpu();
    }
  }

  public void combineCpu() {
    List<CpuData> metrics = IntStream.range(0, 5)
      .mapToObj(i -> RandomMetric.getRandomCpuData())
      .collect(Collectors.toList());

    CpuData combined = CpuData.combine(metrics);

    long deltaMillis = metrics.stream()
      .mapToLong(CpuData::getDeltaMillis)
      .sum();

    double datetime = metrics.stream()
      .mapToDouble(m -> (double) m.getDeltaMillis() / deltaMillis * m.getEpochMillisTime())
      .sum();

    double utilization = metrics.stream()
      .mapToDouble(m -> (double) m.getDeltaMillis() / deltaMillis * m.getUtilization())
      .sum();

    double temperature = metrics.stream()
      .mapToDouble(m -> (double) m.getDeltaMillis() / deltaMillis * m.getTemperature())
      .sum();

    Assert.assertEquals(deltaMillis, combined.getDeltaMillis());
    Assert.assertEquals(datetime, combined.getEpochMillisTime(), 1.0);
    Assert.assertEquals(utilization, combined.getUtilization(), 1.0);
    Assert.assertEquals(temperature, combined.getTemperature(), 1.0);
  }

}
