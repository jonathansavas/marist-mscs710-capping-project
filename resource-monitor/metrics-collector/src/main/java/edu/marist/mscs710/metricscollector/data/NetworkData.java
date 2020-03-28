package edu.marist.mscs710.metricscollector.data;

import edu.marist.mscs710.metricscollector.metric.Fields;
import edu.marist.mscs710.metricscollector.metric.Metric;
import edu.marist.mscs710.metricscollector.metric.MetricType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds a snapshot of Network data.
 */
public class NetworkData extends MetricData {
  private static final long BITS_PER_KILOBIT = 1000L;
  private static final int BITS_PER_BYTE = 8;
  private long bytesSent; // During delta millis
  private long bytesRecv; // During delta millis
  private long speed; // Bits per second network capacity, sum over active network interfaces

  /**
   * Constructs a new <tt>NetworkData</tt> with the supplied metrics.
   * @param bytesSent number of bytes sent during this snapshot
   * @param bytesRecv number of bytes received during this snapshot
   * @param speed total network capacity over active network interfaces in bits per second
   * @param deltaMillis time covered by this snapshot
   * @param epochMillisTime epoch milli timestamp of this snapshot
   */
  public NetworkData(long bytesSent, long bytesRecv, long speed, long deltaMillis, long epochMillisTime) {
    this.bytesSent = bytesSent;
    this.bytesRecv = bytesRecv;
    this.speed = speed;
    this.deltaMillis = deltaMillis;
    this.epochMillisTime = epochMillisTime;
  }

  /**
   * Gets the bytes sent during this snapshot
   * @return number of bytes
   */
  public long getBytesSent() {
    return bytesSent;
  }

  /**
   * Gets the bytes received during this snapshot
   * @return number of bytes
   */
  public long getBytesRecv() {
    return bytesRecv;
  }

  /**
   * Gets the total network capacity over active network interfaces in bits per second.
   * A network interface is considered inactive after five minutes of inactivity.
   * @return speed in bits per second
   */
  public long getSpeed() {
    return speed;
  }

  @Override
  public String toString() {
    return "NetworkData{" +
      "bytesSent=" + bytesSent +
      ", bytesRecv=" + bytesRecv +
      ", speed=" + speed +
      ", deltaMillis=" + deltaMillis +
      ", epochMillisTime=" + epochMillisTime +
      '}';
  }

  private Map<String, Object> getNetworkMap() {
    return new HashMap<String, Object>() {
      {
        put(Fields.Network.DATETIME.toString(), epochMillisTime);
        put(Fields.Network.DELTA_MILLIS.toString(), deltaMillis);
        put(Fields.Network.THROUGHPUT.toString(), speed / BITS_PER_KILOBIT);
        put(Fields.Network.SEND.toString(), ((double) bytesSent) / deltaMillis * BITS_PER_BYTE);
        put(Fields.Network.RECEIVE.toString(), ((double) bytesRecv) / deltaMillis * BITS_PER_BYTE);
      }
    };
  }

  @Override
  public List<Metric> toMetricRecords() {
    return Collections.singletonList(new Metric(MetricType.NETWORK, getNetworkMap()));
  }
}
