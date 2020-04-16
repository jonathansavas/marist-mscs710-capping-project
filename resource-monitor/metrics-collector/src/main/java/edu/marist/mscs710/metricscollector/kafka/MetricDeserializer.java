package edu.marist.mscs710.metricscollector.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.marist.mscs710.metricscollector.Metric;
import edu.marist.mscs710.metricscollector.data.*;
import edu.marist.mscs710.metricscollector.metric.Fields;
import edu.marist.mscs710.metricscollector.metric.NullMetric;
import edu.marist.mscs710.metricscollector.system.SystemConstants;
import edu.marist.mscs710.metricscollector.utils.LoggerUtils;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to deserialize <tt>Metric</tt> objects from Kafka.
 */
public class MetricDeserializer implements Deserializer<Metric> {
  private static final Logger LOGGER = LoggerFactory.getLogger(MetricDeserializer.class);
  private ObjectMapper objectMapper = new ObjectMapper();

  private static final Map<String, Class<? extends Metric>> metricTypes =
    new HashMap<String, Class<? extends Metric>>() {{
      put(Fields.METRIC_TYPE_CPU, CpuData.class);
      put(Fields.METRIC_TYPE_CPU_CORE, CpuCoreData.class);
      put(Fields.METRIC_TYPE_MEMORY, MemoryData.class);
      put(Fields.METRIC_TYPE_NETWORK, NetworkData.class);
      put(Fields.METRIC_TYPE_PROCESSES, ProcessData.class);
      put(Fields.METRIC_TYPE_SYSTEM_METRICS, SystemData.class);
      put(Fields.METRIC_TYPE_SYSTEM_CONSTANTS, SystemConstants.class);
  }};

  @Override
  public Metric deserialize(String s, byte[] bytes) {
    try {
      JsonNode json = objectMapper.readTree(bytes);
      JsonNode metricType = json.get(Fields.METRIC_TYPE);

      if (metricType == null)
        return new NullMetric();

      Class<? extends Metric> clazz = metricTypes.get(metricType.asText());

      if (clazz == null)
        return new NullMetric();

      return objectMapper.treeToValue(json, clazz);
    } catch (IOException ex) {
      LOGGER.error(LoggerUtils.getExceptionMessage(ex));
      return new NullMetric();
    }
  }
}
