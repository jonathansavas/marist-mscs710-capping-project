package edu.marist.mscs710.metricscollector;

import edu.marist.mscs710.metricscollector.kafka.MetricDeserializer;
import edu.marist.mscs710.metricscollector.kafka.MetricSerializer;
import edu.marist.mscs710.metricscollector.metric.Fields;
import edu.marist.mscs710.metricscollector.metric.Metric;
import edu.marist.mscs710.metricscollector.metric.MetricType;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class KafkaUtilsTest {

  @Test
  public void testMetricSerializerDeserializer() {
    Metric before = new Metric(MetricType.SYSTEM_METRICS, new HashMap<String, Object>() {{
      put(Fields.SystemMetrics.UPTIME.toString(), 999999);
      put(Fields.SystemMetrics.DATETIME.toString(), 111111);
    }});

    Metric after = new MetricDeserializer().deserialize("", new MetricSerializer().serialize("", before));

    Assert.assertEquals(before.getMetricType(), after.getMetricType());

    Assert.assertEquals(
      before.getMetricData().get(Fields.SystemMetrics.UPTIME.toString()),
      after.getMetricData().get(Fields.SystemMetrics.UPTIME.toString())
    );

    Assert.assertEquals(
      before.getMetricData().get(Fields.SystemMetrics.DATETIME.toString()),
      after.getMetricData().get(Fields.SystemMetrics.DATETIME.toString())
    );
  }
}
