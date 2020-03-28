package edu.marist.mscs710.metricscollector.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.marist.mscs710.metricscollector.metric.Metric;
import edu.marist.mscs710.metricscollector.metric.MetricType;
import edu.marist.mscs710.metricscollector.utils.LoggerUtils;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.util.HashMap;

/**
 * Class to deserialize <tt>Metric</tt> objects from Kafka.
 */
public class MetricDeserializer implements Deserializer<Metric> {
  private static final Logger LOGGER = LoggerFactory.getLogger(MetricDeserializer.class);
  private ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Metric deserialize(String s, byte[] bytes) {
    try {
      return objectMapper.readValue(bytes, Metric.class);
    } catch (IOException ex) {
      LOGGER.error(LoggerUtils.getExceptionMessage(ex));
      return new Metric(MetricType.NULL, new HashMap<>());
    }
  }
}
