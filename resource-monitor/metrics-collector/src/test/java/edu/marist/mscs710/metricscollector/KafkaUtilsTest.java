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
import java.util.Random;
import java.util.UUID;

public class KafkaUtilsTest {
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final MetricSerializer metricSerializer = new MetricSerializer();
  private static final MetricDeserializer metricDeserializer = new MetricDeserializer();

  private static Random ran = new Random();

  @Test
  public void testCpuCoreDeserialization() throws IOException {
    CpuCoreData metricData = new CpuCoreData(ran.nextInt(), ran.nextDouble(), ran.nextLong(), ran.nextLong());
    byte[] ser = metricSerializer.serialize("", metricData);

    Map<String, Object> deser = deserializeToMap(ser);

    Assert.assertEquals(Fields.METRIC_TYPE_CPU_CORE, deser.get(Fields.METRIC_TYPE));
    Assert.assertTrue(metricData.getCoreId() == (Integer) deser.get(Fields.CPU_CORE_CORE_ID));
    Assert.assertTrue(metricData.getCoreUtilization() == (Double) deser.get(Fields.CPU_CORE_CORE_UTILIZATION));
    Assert.assertTrue(metricData.getDeltaMillis() == (Long) deser.get(Fields.CPU_CORE_DELTA_MILLIS));
    Assert.assertTrue(metricData.getEpochMillisTime() == (Long) deser.get(Fields.CPU_CORE_DATETIME));

    CpuCoreData deserMetric = (CpuCoreData) metricDeserializer.deserialize("", ser);

    Assert.assertEquals(metricData.getEpochMillisTime(), deserMetric.getEpochMillisTime());
    Assert.assertEquals(metricData.getDeltaMillis(), deserMetric.getDeltaMillis());
    Assert.assertEquals(metricData.getCoreUtilization(), deserMetric.getCoreUtilization(), 0.0);
    Assert.assertEquals(metricData.getCoreId(), deserMetric.getCoreId());
  }

  @Test
  public void testCpuDeserialization() throws IOException {
    CpuData metricData = new CpuData(ran.nextDouble(), ran.nextDouble(), ran.nextLong(), ran.nextLong());
    byte[] ser = metricSerializer.serialize("", metricData);

    Map<String, Object> deser = deserializeToMap(ser);

    Assert.assertEquals(Fields.METRIC_TYPE_CPU, deser.get(Fields.METRIC_TYPE));
    Assert.assertTrue(metricData.getUtilization() == (Double) deser.get(Fields.CPU_UTILIZATION));
    Assert.assertTrue(metricData.getTemperature() == (Double) deser.get(Fields.CPU_TEMPERATURE));
    Assert.assertTrue(metricData.getDeltaMillis() == (Long) deser.get(Fields.CPU_DELTA_MILLIS));
    Assert.assertTrue(metricData.getEpochMillisTime() == (Long) deser.get(Fields.CPU_DATETIME));

    CpuData deserMetric = (CpuData) metricDeserializer.deserialize("", ser);

    Assert.assertEquals(metricData.getEpochMillisTime(), deserMetric.getEpochMillisTime());
    Assert.assertEquals(metricData.getDeltaMillis(), deserMetric.getDeltaMillis());
    Assert.assertEquals(metricData.getUtilization(), deserMetric.getUtilization(), 0.0);
    Assert.assertEquals(metricData.getTemperature(), deserMetric.getTemperature(), 0.0);
  }

  @Test
  public void testMemoryDeserialization() throws IOException {
    MemoryData metricData = new MemoryData(ran.nextDouble(), ran.nextDouble(), ran.nextLong(), ran.nextLong());

    byte[] ser = metricSerializer.serialize("", metricData);

    Map<String, Object> deser = deserializeToMap(ser);

    Assert.assertEquals(Fields.METRIC_TYPE_MEMORY, deser.get(Fields.METRIC_TYPE));
    Assert.assertTrue(metricData.getMemoryUtilization() == (Double) deser.get(Fields.MEMORY_UTILIZATION));
    Assert.assertTrue(metricData.getPageFaults() == (Double) deser.get(Fields.MEMORY_PAGE_FAULTS));
    Assert.assertTrue(metricData.getDeltaMillis() == (Long) deser.get(Fields.MEMORY_DELTA_MILLIS));
    Assert.assertTrue(metricData.getEpochMillisTime() == (Long) deser.get(Fields.MEMORY_DATETIME));

    MemoryData deserMetric = (MemoryData) metricDeserializer.deserialize("", ser);

    Assert.assertEquals(metricData.getEpochMillisTime(), deserMetric.getEpochMillisTime());
    Assert.assertEquals(metricData.getDeltaMillis(), deserMetric.getDeltaMillis());
    Assert.assertEquals(metricData.getMemoryUtilization(), deserMetric.getMemoryUtilization(), 0.0);
    Assert.assertEquals(metricData.getPageFaults(), deserMetric.getPageFaults(), 0.0);
  }

  @Test
  public void testNetworkDeserialization() throws IOException {
    NetworkData metricData = new NetworkData(ran.nextDouble(), ran.nextDouble(), ran.nextLong(), ran.nextLong(), ran.nextLong());

    byte[] ser = metricSerializer.serialize("", metricData);

    Map<String, Object> deser = deserializeToMap(ser);

    Assert.assertEquals(Fields.METRIC_TYPE_NETWORK, deser.get(Fields.METRIC_TYPE));
    Assert.assertTrue(metricData.getThroughput() == (Long) deser.get(Fields.NETWORK_THROUGHPUT));
    Assert.assertTrue(metricData.getSend() == (Double) deser.get(Fields.NETWORK_SEND));
    Assert.assertTrue(metricData.getReceive() == (Double) deser.get(Fields.NETWORK_RECEIVE));
    Assert.assertTrue(metricData.getDeltaMillis() == (Long) deser.get(Fields.NETWORK_DELTA_MILLIS));
    Assert.assertTrue(metricData.getEpochMillisTime() == (Long) deser.get(Fields.NETWORK_DATETIME));

    NetworkData deserMetric = (NetworkData) metricDeserializer.deserialize("", ser);

    Assert.assertEquals(metricData.getEpochMillisTime(), deserMetric.getEpochMillisTime());
    Assert.assertEquals(metricData.getDeltaMillis(), deserMetric.getDeltaMillis());
    Assert.assertEquals(metricData.getReceive(), deserMetric.getReceive(), 0.0);
    Assert.assertEquals(metricData.getSend(), deserMetric.getSend(), 0.0);
    Assert.assertEquals(metricData.getThroughput(), deserMetric.getThroughput());
  }

  @Test
  public void testProcessDeserialization() throws IOException {
    ProcessData metricData = new ProcessData(ran.nextInt(), UUID.randomUUID().toString(), ran.nextLong(), ran.nextLong(), ran.nextDouble(),
      ran.nextLong(), ran.nextDouble(), ran.nextDouble(), Processes.PidState.RUNNING, ran.nextLong(), ran.nextLong());

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

    Assert.assertEquals(metricData.getEpochMillisTime(), deserMetric.getEpochMillisTime());
    Assert.assertEquals(metricData.getDeltaMillis(), deserMetric.getDeltaMillis());
    Assert.assertEquals(metricData.getCpuUsage(), deserMetric.getCpuUsage(), 0.0);
    Assert.assertEquals(metricData.getKbRead(), deserMetric.getKbRead(), 0.0);
    Assert.assertEquals(metricData.getKbWritten(), deserMetric.getKbWritten(), 0.0);
    Assert.assertEquals(metricData.getName(), deserMetric.getName());
    Assert.assertEquals(metricData.getStartTime(), deserMetric.getStartTime());
    Assert.assertEquals(metricData.getUpTime(), deserMetric.getUpTime());
    Assert.assertEquals(metricData.getMemory(), deserMetric.getMemory());
    Assert.assertEquals(metricData.getPidState(), deserMetric.getPidState());
    Assert.assertEquals(metricData.getPid(), deserMetric.getPid());
  }

  @Test
  public void testSystemMetricsDeserialization() throws IOException {
    SystemData metricData = new SystemData(ran.nextLong(), ran.nextLong(), ran.nextLong());

    byte[] ser = metricSerializer.serialize("", metricData);

    Map<String, Object> deser = deserializeToMap(ser);

    Assert.assertEquals(Fields.METRIC_TYPE_SYSTEM_METRICS, deser.get(Fields.METRIC_TYPE));
    Assert.assertTrue(metricData.getUpTime() == (Long) deser.get(Fields.SYSTEM_METRICS_UPTIME));
    Assert.assertTrue(metricData.getDeltaMillis() == (Long) deser.get(Fields.SYSTEM_METRICS_DELTA_MILLIS));
    Assert.assertTrue(metricData.getEpochMillisTime() == (Long) deser.get(Fields.SYSTEM_METRICS_DATETIME));

    SystemData deserMetric = (SystemData) metricDeserializer.deserialize("", ser);

    Assert.assertEquals(metricData.getEpochMillisTime(), deserMetric.getEpochMillisTime());
    Assert.assertEquals(metricData.getDeltaMillis(), deserMetric.getDeltaMillis());
    Assert.assertEquals(metricData.getUpTime(), deserMetric.getUpTime());
  }

  @Test
  public void testSystemConstantsDeserialization() throws IOException {
    SystemConstants metricData = new SystemConstants(ran.nextDouble(), ran.nextInt(), ran.nextInt(), ran.nextDouble());

    byte[] ser = metricSerializer.serialize("", metricData);

    Map<String, Object> deser = deserializeToMap(ser);

    Assert.assertEquals(Fields.METRIC_TYPE_SYSTEM_CONSTANTS, deser.get(Fields.METRIC_TYPE));
    Assert.assertTrue(metricData.getTotalMemGb() == (Double) deser.get(Fields.SYSTEM_CONSTANTS_TOTAL_MEMORY));
    Assert.assertTrue(metricData.getPhysicalCores() == (Integer) deser.get(Fields.SYSTEM_CONSTANTS_PHYSICAL_CORES));
    Assert.assertTrue(metricData.getLogicalCores() == (Integer) deser.get(Fields.SYSTEM_CONSTANTS_LOGICAL_CORES));
    Assert.assertTrue(metricData.getCpuSpeed() == (Double) deser.get(Fields.SYSTEM_CONSTANTS_CPU_SPEED));

    SystemConstants deserMetric = (SystemConstants) metricDeserializer.deserialize("", ser);

    Assert.assertEquals(metricData.getPhysicalCores(), deserMetric.getPhysicalCores());
    Assert.assertEquals(metricData.getLogicalCores(), deserMetric.getLogicalCores());
    Assert.assertEquals(metricData.getTotalMemGb(), deserMetric.getTotalMemGb(), 0.0);
    Assert.assertEquals(metricData.getCpuSpeed(), deserMetric.getCpuSpeed(), 0.0);
  }

  private Map<String, Object> deserializeToMap(byte[] serializedJson) throws IOException {
    TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {};

    return objectMapper.readValue(serializedJson, typeRef);
  }
}
