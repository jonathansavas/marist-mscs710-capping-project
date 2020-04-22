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
    Assert.assertEquals(metricData.getCoreId(), deser.get(Fields.CPU_CORE_CORE_ID));
    Assert.assertEquals(metricData.getCoreUtilization(), deser.get(Fields.CPU_CORE_CORE_UTILIZATION));
    Assert.assertEquals(metricData.getDeltaMillis(), Long.parseLong(deser.get(Fields.CPU_CORE_DELTA_MILLIS).toString()));
    Assert.assertEquals(metricData.getEpochMillisTime(), Long.parseLong(deser.get(Fields.CPU_CORE_DATETIME).toString()));

    CpuCoreData deserMetric = (CpuCoreData) metricDeserializer.deserialize("", ser);

    Assert.assertEquals(metricData, deserMetric);
  }

  @Test
  public void testCpuDeserialization() throws IOException {
    CpuData metricData = RandomMetric.getRandomCpuData();
    byte[] ser = metricSerializer.serialize("", metricData);

    Map<String, Object> deser = deserializeToMap(ser);

    Assert.assertEquals(Fields.METRIC_TYPE_CPU, deser.get(Fields.METRIC_TYPE));
    Assert.assertEquals(metricData.getUtilization(), deser.get(Fields.CPU_UTILIZATION));
    Assert.assertEquals(metricData.getTemperature(), deser.get(Fields.CPU_TEMPERATURE));
    Assert.assertEquals(metricData.getDeltaMillis(), Long.parseLong(deser.get(Fields.CPU_DELTA_MILLIS).toString()));
    Assert.assertEquals(metricData.getEpochMillisTime(), Long.parseLong(deser.get(Fields.CPU_DATETIME).toString()));

    CpuData deserMetric = (CpuData) metricDeserializer.deserialize("", ser);

    Assert.assertEquals(metricData, deserMetric);
  }

  @Test
  public void testMemoryDeserialization() throws IOException {
    MemoryData metricData = RandomMetric.getRandomMemoryData();

    byte[] ser = metricSerializer.serialize("", metricData);

    Map<String, Object> deser = deserializeToMap(ser);

    Assert.assertEquals(Fields.METRIC_TYPE_MEMORY, deser.get(Fields.METRIC_TYPE));
    Assert.assertEquals(metricData.getMemoryUtilization(), deser.get(Fields.MEMORY_UTILIZATION));
    Assert.assertEquals(metricData.getPageFaults(), deser.get(Fields.MEMORY_PAGE_FAULTS));
    Assert.assertEquals(metricData.getDeltaMillis(), Long.parseLong(deser.get(Fields.MEMORY_DELTA_MILLIS).toString()));
    Assert.assertEquals(metricData.getEpochMillisTime(), Long.parseLong(deser.get(Fields.MEMORY_DATETIME).toString()));

    MemoryData deserMetric = (MemoryData) metricDeserializer.deserialize("", ser);

    Assert.assertEquals(metricData, deserMetric);
  }

  @Test
  public void testNetworkDeserialization() throws IOException {
    NetworkData metricData = RandomMetric.getRandomNetworkData();

    byte[] ser = metricSerializer.serialize("", metricData);

    Map<String, Object> deser = deserializeToMap(ser);

    Assert.assertEquals(Fields.METRIC_TYPE_NETWORK, deser.get(Fields.METRIC_TYPE));
    Assert.assertEquals(metricData.getThroughput(), Long.parseLong(deser.get(Fields.NETWORK_THROUGHPUT).toString()));
    Assert.assertEquals(metricData.getSend(), deser.get(Fields.NETWORK_SEND));
    Assert.assertEquals(metricData.getReceive(), deser.get(Fields.NETWORK_RECEIVE));
    Assert.assertEquals(metricData.getDeltaMillis(), Long.parseLong(deser.get(Fields.NETWORK_DELTA_MILLIS).toString()));
    Assert.assertEquals(metricData.getEpochMillisTime(), Long.parseLong(deser.get(Fields.NETWORK_DATETIME).toString()));

    NetworkData deserMetric = (NetworkData) metricDeserializer.deserialize("", ser);

    Assert.assertEquals(metricData, deserMetric);
  }

  @Test
  public void testProcessDeserialization() throws IOException {
    ProcessData metricData = RandomMetric.getRandomProcessData();

    byte[] ser = metricSerializer.serialize("", metricData);

    Map<String, Object> deser = deserializeToMap(ser);

    Assert.assertEquals(Fields.METRIC_TYPE_PROCESSES, deser.get(Fields.METRIC_TYPE));
    Assert.assertEquals(metricData.getPid(), deser.get(Fields.PROCESSES_PID));
    Assert.assertEquals(metricData.getName(), deser.get(Fields.PROCESSES_NAME).toString());
    Assert.assertEquals(metricData.getStartTime(), Long.parseLong(deser.get(Fields.PROCESSES_START_TIME).toString()));
    Assert.assertEquals(metricData.getUpTime(), Long.parseLong(deser.get(Fields.PROCESSES_UPTIME).toString()));
    Assert.assertEquals(metricData.getCpuUsage(), deser.get(Fields.PROCESSES_CPU_USAGE));
    Assert.assertEquals(metricData.getMemory(), Long.parseLong(deser.get(Fields.PROCESSES_MEMORY).toString()));
    Assert.assertEquals(metricData.getKbRead(), deser.get(Fields.PROCESSES_KB_READ));
    Assert.assertEquals(metricData.getKbWritten(), deser.get(Fields.PROCESSES_KB_WRITTEN));
    Assert.assertSame(metricData.getPidState(), Processes.PidState.valueOf(deser.get(Fields.PROCESSES_STATE).toString()));
    Assert.assertEquals(metricData.getDeltaMillis(), Long.parseLong(deser.get(Fields.PROCESSES_DELTA_MILLIS).toString()));
    Assert.assertEquals(metricData.getEpochMillisTime(), Long.parseLong(deser.get(Fields.PROCESSES_DATETIME).toString()));

    ProcessData deserMetric = (ProcessData) metricDeserializer.deserialize("", ser);

    Assert.assertEquals(metricData, deserMetric);
  }

  @Test
  public void testSystemMetricsDeserialization() throws IOException {
    SystemData metricData = RandomMetric.getRandomSystemData();

    byte[] ser = metricSerializer.serialize("", metricData);

    Map<String, Object> deser = deserializeToMap(ser);

    Assert.assertEquals(Fields.METRIC_TYPE_SYSTEM_METRICS, deser.get(Fields.METRIC_TYPE));
    Assert.assertEquals(metricData.getUpTime(), Long.parseLong(deser.get(Fields.SYSTEM_METRICS_UPTIME).toString()));
    Assert.assertEquals(metricData.getDeltaMillis(), Long.parseLong(deser.get(Fields.SYSTEM_METRICS_DELTA_MILLIS).toString()));
    Assert.assertEquals(metricData.getEpochMillisTime(), Long.parseLong(deser.get(Fields.SYSTEM_METRICS_DATETIME).toString()));

    SystemData deserMetric = (SystemData) metricDeserializer.deserialize("", ser);

    Assert.assertEquals(metricData, deserMetric);
  }

  @Test
  public void testSystemConstantsDeserialization() throws IOException {
    SystemConstants metricData = RandomMetric.getRandomSystemConstants();

    byte[] ser = metricSerializer.serialize("", metricData);

    Map<String, Object> deser = deserializeToMap(ser);

    Assert.assertEquals(Fields.METRIC_TYPE_SYSTEM_CONSTANTS, deser.get(Fields.METRIC_TYPE));
    Assert.assertEquals(metricData.getTotalMemGb(), deser.get(Fields.SYSTEM_CONSTANTS_TOTAL_MEMORY));
    Assert.assertEquals(metricData.getPhysicalCores(), deser.get(Fields.SYSTEM_CONSTANTS_PHYSICAL_CORES));
    Assert.assertEquals(metricData.getLogicalCores(), deser.get(Fields.SYSTEM_CONSTANTS_LOGICAL_CORES));
    Assert.assertEquals(metricData.getCpuSpeed(), deser.get(Fields.SYSTEM_CONSTANTS_CPU_SPEED));

    SystemConstants deserMetric = (SystemConstants) metricDeserializer.deserialize("", ser);

    Assert.assertEquals(metricData, deserMetric);
  }

  private Map<String, Object> deserializeToMap(byte[] serializedJson) throws IOException {
    TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {};

    return objectMapper.readValue(serializedJson, typeRef);
  }
}
