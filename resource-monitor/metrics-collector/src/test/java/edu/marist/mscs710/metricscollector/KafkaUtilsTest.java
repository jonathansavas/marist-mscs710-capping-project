package edu.marist.mscs710.metricscollector;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.marist.mscs710.metricscollector.data.*;
import edu.marist.mscs710.metricscollector.kafka.MetricDeserializer;
import edu.marist.mscs710.metricscollector.kafka.MetricSerializer;
import edu.marist.mscs710.metricscollector.metric.Fields;
import edu.marist.mscs710.metricscollector.system.Processes;
import edu.marist.mscs710.metricscollector.system.SystemConstants;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class KafkaUtilsTest {
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final MetricSerializer metricSerializer = new MetricSerializer();
  private static final MetricDeserializer metricDeserializer = new MetricDeserializer();

  @Test
  public void testCpuCoreDeserialization() throws IOException {
    CpuCoreData metricData = RandomMetric.getRandomCpuCoreData();
    byte[] ser = metricSerializer.serialize("", metricData);

    Map<String, Object> deser = deserializeToMap(ser);

    Assert.assertEquals(Fields.METRIC_TYPE_CPU_CORE, deser.get(Fields.METRIC_TYPE));
    Assert.assertTrue(metricData.getCoreId() == (Integer) deser.get(Fields.CPU_CORE_CORE_ID));
    Assert.assertTrue(metricData.getCoreUtilization() == (Double) deser.get(Fields.CPU_CORE_CORE_UTILIZATION));
    Assert.assertTrue(metricData.getDeltaMillis() == (Long) deser.get(Fields.CPU_CORE_DELTA_MILLIS));
    Assert.assertTrue(metricData.getEpochMillisTime() == (Long) deser.get(Fields.CPU_CORE_DATETIME));

    CpuCoreData deserMetric = (CpuCoreData) metricDeserializer.deserialize("", ser);

    Assert.assertEquals(metricData, deserMetric);
  }

  @Test
  public void testCpuDeserialization() throws IOException {
    CpuData metricData = RandomMetric.getRandomCpuData();
    byte[] ser = metricSerializer.serialize("", metricData);

    Map<String, Object> deser = deserializeToMap(ser);

    Assert.assertEquals(Fields.METRIC_TYPE_CPU, deser.get(Fields.METRIC_TYPE));
    Assert.assertTrue(metricData.getUtilization() == (Double) deser.get(Fields.CPU_UTILIZATION));
    Assert.assertTrue(metricData.getTemperature() == (Double) deser.get(Fields.CPU_TEMPERATURE));
    Assert.assertTrue(metricData.getDeltaMillis() == (Long) deser.get(Fields.CPU_DELTA_MILLIS));
    Assert.assertTrue(metricData.getEpochMillisTime() == (Long) deser.get(Fields.CPU_DATETIME));

    CpuData deserMetric = (CpuData) metricDeserializer.deserialize("", ser);

    Assert.assertEquals(metricData, deserMetric);
  }

  @Test
  public void testMemoryDeserialization() throws IOException {
    MemoryData metricData = RandomMetric.getRandomMemoryData();

    byte[] ser = metricSerializer.serialize("", metricData);

    Map<String, Object> deser = deserializeToMap(ser);

    Assert.assertEquals(Fields.METRIC_TYPE_MEMORY, deser.get(Fields.METRIC_TYPE));
    Assert.assertTrue(metricData.getMemoryUtilization() == (Double) deser.get(Fields.MEMORY_UTILIZATION));
    Assert.assertTrue(metricData.getPageFaults() == (Double) deser.get(Fields.MEMORY_PAGE_FAULTS));
    Assert.assertTrue(metricData.getDeltaMillis() == (Long) deser.get(Fields.MEMORY_DELTA_MILLIS));
    Assert.assertTrue(metricData.getEpochMillisTime() == (Long) deser.get(Fields.MEMORY_DATETIME));

    MemoryData deserMetric = (MemoryData) metricDeserializer.deserialize("", ser);

    Assert.assertEquals(metricData, deserMetric);
  }

  @Test
  public void testNetworkDeserialization() throws IOException {
    NetworkData metricData = RandomMetric.getRandomNetworkData();

    byte[] ser = metricSerializer.serialize("", metricData);

    Map<String, Object> deser = deserializeToMap(ser);

    Assert.assertEquals(Fields.METRIC_TYPE_NETWORK, deser.get(Fields.METRIC_TYPE));
    Assert.assertTrue(metricData.getThroughput() == (Long) deser.get(Fields.NETWORK_THROUGHPUT));
    Assert.assertTrue(metricData.getSend() == (Double) deser.get(Fields.NETWORK_SEND));
    Assert.assertTrue(metricData.getReceive() == (Double) deser.get(Fields.NETWORK_RECEIVE));
    Assert.assertTrue(metricData.getDeltaMillis() == (Long) deser.get(Fields.NETWORK_DELTA_MILLIS));
    Assert.assertTrue(metricData.getEpochMillisTime() == (Long) deser.get(Fields.NETWORK_DATETIME));

    NetworkData deserMetric = (NetworkData) metricDeserializer.deserialize("", ser);

    Assert.assertEquals(metricData, deserMetric);
  }

  @Test
  public void testProcessDeserialization() throws IOException {
    ProcessData metricData = RandomMetric.getRandomProcessData();

    byte[] ser = metricSerializer.serialize("", metricData);

    Map<String, Object> deser = deserializeToMap(ser);

    Assert.assertEquals(Fields.METRIC_TYPE_PROCESSES, deser.get(Fields.METRIC_TYPE));
    Assert.assertTrue(metricData.getPid() == (Integer) deser.get(Fields.PROCESSES_PID));
    Assert.assertEquals(metricData.getName(), deser.get(Fields.PROCESSES_NAME).toString());
    Assert.assertTrue(metricData.getStartTime() == (Long) deser.get(Fields.PROCESSES_START_TIME));
    Assert.assertTrue(metricData.getUpTime() == (Long) deser.get(Fields.PROCESSES_UPTIME));
    Assert.assertTrue(metricData.getCpuUsage() == (Double) deser.get(Fields.PROCESSES_CPU_USAGE));
    Assert.assertTrue(metricData.getMemory() == (Long) deser.get(Fields.PROCESSES_MEMORY));
    Assert.assertTrue(metricData.getKbRead() == (Double) deser.get(Fields.PROCESSES_KB_READ));
    Assert.assertTrue(metricData.getKbWritten() == (Double) deser.get(Fields.PROCESSES_KB_WRITTEN));
    Assert.assertTrue(metricData.getPidState() == Processes.PidState.valueOf(deser.get(Fields.PROCESSES_STATE).toString()));
    Assert.assertTrue(metricData.getDeltaMillis() == (Long) deser.get(Fields.PROCESSES_DELTA_MILLIS));
    Assert.assertTrue(metricData.getEpochMillisTime() == (Long) deser.get(Fields.PROCESSES_DATETIME));

    ProcessData deserMetric = (ProcessData) metricDeserializer.deserialize("", ser);

    Assert.assertEquals(metricData, deserMetric);
  }

  @Test
  public void testSystemMetricsDeserialization() throws IOException {
    SystemData metricData = RandomMetric.getRandomSystemData();

    byte[] ser = metricSerializer.serialize("", metricData);

    Map<String, Object> deser = deserializeToMap(ser);

    Assert.assertEquals(Fields.METRIC_TYPE_SYSTEM_METRICS, deser.get(Fields.METRIC_TYPE));
    Assert.assertTrue(metricData.getUpTime() == (Long) deser.get(Fields.SYSTEM_METRICS_UPTIME));
    Assert.assertTrue(metricData.getDeltaMillis() == (Long) deser.get(Fields.SYSTEM_METRICS_DELTA_MILLIS));
    Assert.assertTrue(metricData.getEpochMillisTime() == (Long) deser.get(Fields.SYSTEM_METRICS_DATETIME));

    SystemData deserMetric = (SystemData) metricDeserializer.deserialize("", ser);

    Assert.assertEquals(metricData, deserMetric);
  }

  @Test
  public void testSystemConstantsDeserialization() throws IOException {
    SystemConstants metricData = RandomMetric.getRandomSystemConstants();

    byte[] ser = metricSerializer.serialize("", metricData);

    Map<String, Object> deser = deserializeToMap(ser);

    Assert.assertEquals(Fields.METRIC_TYPE_SYSTEM_CONSTANTS, deser.get(Fields.METRIC_TYPE));
    Assert.assertTrue(metricData.getTotalMemGb() == (Double) deser.get(Fields.SYSTEM_CONSTANTS_TOTAL_MEMORY));
    Assert.assertTrue(metricData.getPhysicalCores() == (Integer) deser.get(Fields.SYSTEM_CONSTANTS_PHYSICAL_CORES));
    Assert.assertTrue(metricData.getLogicalCores() == (Integer) deser.get(Fields.SYSTEM_CONSTANTS_LOGICAL_CORES));
    Assert.assertTrue(metricData.getCpuSpeed() == (Double) deser.get(Fields.SYSTEM_CONSTANTS_CPU_SPEED));

    SystemConstants deserMetric = (SystemConstants) metricDeserializer.deserialize("", ser);

    Assert.assertEquals(metricData, deserMetric);
  }

  private Map<String, Object> deserializeToMap(byte[] serializedJson) throws IOException {
    TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {};

    return objectMapper.readValue(serializedJson, typeRef);
  }
}
