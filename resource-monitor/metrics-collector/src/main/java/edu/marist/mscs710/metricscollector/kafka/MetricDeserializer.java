package edu.marist.mscs710.metricscollector.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.marist.mscs710.metricscollector.Metric;
import edu.marist.mscs710.metricscollector.data.*;
import edu.marist.mscs710.metricscollector.metric.Fields;
import edu.marist.mscs710.metricscollector.metric.NullMetric;
import edu.marist.mscs710.metricscollector.system.SystemConstants;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class to deserialize <tt>Metric</tt> objects. This class is
 * not thread safe.
 */
public class MetricDeserializer implements Deserializer<Metric> {
  private static final Logger LOGGER = LoggerFactory.getLogger(MetricDeserializer.class);
  private ObjectMapper objectMapper = new ObjectMapper();

  private static final Map<String, Class<? extends Metric>> metricTypesToClasses =
    new HashMap<String, Class<? extends Metric>>() {{
      put(Fields.METRIC_TYPE_CPU, CpuData.class);
      put(Fields.METRIC_TYPE_CPU_CORE, CpuCoreData.class);
      put(Fields.METRIC_TYPE_MEMORY, MemoryData.class);
      put(Fields.METRIC_TYPE_NETWORK, NetworkData.class);
      put(Fields.METRIC_TYPE_PROCESSES, ProcessData.class);
      put(Fields.METRIC_TYPE_SYSTEM_METRICS, SystemData.class);
      put(Fields.METRIC_TYPE_SYSTEM_CONSTANTS, SystemConstants.class);
  }};

  private static final Map<Class<? extends Metric>, String> classesToMetricTypes =
    metricTypesToClasses.entrySet().stream()
    .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

  @Override
  public Metric deserialize(String s, byte[] bytes) {
    try {
      JsonNode json = objectMapper.readTree(bytes);
      JsonNode metricType = json.get(Fields.METRIC_TYPE);

      if (metricType == null)
        return new NullMetric();

      Class<? extends Metric> clazz = metricTypesToClasses.get(metricType.asText());

      if (clazz == null)
        return new NullMetric();

      return deserialize(json, clazz);
    } catch (IOException ex) {
      LOGGER.error(ex.getMessage(), ex);
      return new NullMetric();
    }
  }

  /**
   * Deserializes a <tt>JsonNode</tt> to the specified class.
   *
   * @param json  json data to deserialize
   * @param clazz class to hold the json data
   * @param <T>   <tt>Metric</tt>
   * @return deserialized data
   * @throws JsonProcessingException if the data cannot be deserialized to <tt>T</tt>
   */
  public <T extends Metric> T deserialize(JsonNode json, Class<T> clazz) throws JsonProcessingException {
    if (json.get(Fields.METRIC_TYPE) == null)
      ((ObjectNode) json).put(Fields.METRIC_TYPE, classesToMetricTypes.get(clazz));

    return objectMapper.treeToValue(json, clazz);
  }

  /**
   * Gets the class type associated with a <tt>metricType</tt>.
   *
   * @param metricType type of the metric
   * @return class of <tt>metricType</tt>, or null if mapping cannot be found
   */
  public static Class<? extends Metric> lookupMetricClass(String metricType) {
    return metricTypesToClasses.get(metricType);
  }

  /**
   * Gets the metric type associated with a certain <tt>Metric</tt> class
   *
   * @param clazz <tt>Metric</tt> class
   * @return metric type, or null if mapping cannot be found
   */
  public static String lookupMetricType(Class<? extends Metric> clazz) {
    return classesToMetricTypes.get(clazz);
  }
}
