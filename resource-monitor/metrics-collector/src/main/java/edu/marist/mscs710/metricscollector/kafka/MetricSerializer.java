package edu.marist.mscs710.metricscollector.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.marist.mscs710.metricscollector.Metric;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to serialize <tt>Metric</tt> objects.
 */
public class MetricSerializer implements Serializer<Metric> {
  private static final Logger LOGGER = LoggerFactory.getLogger(MetricSerializer.class);
  private ObjectMapper objectMapper = new ObjectMapper();

  public byte[] serialize(String s, Metric metric) {
    try {
      return objectMapper.writeValueAsBytes(metric);
    } catch (JsonProcessingException ex) {
      LOGGER.error(ex.getMessage(), ex);
      return new byte[0];
    }
  }
}
