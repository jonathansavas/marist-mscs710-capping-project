package edu.marist.mscs710.metricscollector.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.marist.mscs710.metricscollector.metric.Metric;
import edu.marist.mscs710.metricscollector.metric.MetricType;
import org.apache.kafka.common.serialization.Deserializer;


import java.io.IOException;
import java.util.HashMap;

public class MetricDeserializer implements Deserializer<Metric> {

  @Override
  public Metric deserialize(String s, byte[] bytes) {
    try {
      return new ObjectMapper().readValue(bytes, Metric.class);
    } catch (IOException ex) {
      return new Metric(MetricType.NULL, new HashMap<>());
    }
  }
}
