package edu.marist.mscs710.metricscollector.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.marist.mscs710.metricscollector.metric.Metric;
import org.apache.kafka.common.serialization.Serializer;

public class MetricSerializer implements Serializer<Metric> {

  @Override
  public byte[] serialize(String s, Metric metric) {
    try {
      return new ObjectMapper().writeValueAsBytes(metric);
    } catch (JsonProcessingException ex) {
      return new byte[0];
    }
  }
}
