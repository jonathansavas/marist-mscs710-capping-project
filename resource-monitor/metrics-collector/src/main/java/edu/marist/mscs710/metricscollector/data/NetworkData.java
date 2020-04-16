package edu.marist.mscs710.metricscollector.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.marist.mscs710.metricscollector.metric.Fields;
import edu.marist.mscs710.metricscollector.metric.Metric;

import java.util.List;

/**
 * Holds a snapshot of Network data.
 */
public class NetworkData extends MetricData {

  private static final long BITS_PER_KILOBIT = 1000L;

  private static final int BITS_PER_BYTE = 8;

  @JsonProperty(Fields.NETWORK_SEND)
  private double send; // During delta millis

  @JsonProperty(Fields.NETWORK_RECEIVE)
  private double receive; // During delta millis

  @JsonProperty(Fields.NETWORK_THROUGHPUT)
  private long throughput; // Bits per second network capacity, sum over active network interfaces

  /**
   * Constructs a new <tt>NetworkData</tt> with the supplied metrics.
   *
   * @param bytesSent       number of bytes sent during this snapshot
   * @param bytesRecv       number of bytes received during this snapshot
   * @param speed           total network capacity over active network interfaces in bits per second
   * @param deltaMillis     time covered by this snapshot
   * @param epochMillisTime epoch milli timestamp of this snapshot
   */
  public NetworkData(long bytesSent, long bytesRecv, long speed, long deltaMillis, long epochMillisTime) {
    this.send = ((double) bytesSent) / deltaMillis * BITS_PER_BYTE;
    this.receive = ((double) bytesRecv) / deltaMillis * BITS_PER_BYTE;
    this.throughput = speed / BITS_PER_KILOBIT;
    this.deltaMillis = deltaMillis;
    this.epochMillisTime = epochMillisTime;
  }

  /**
   * Gets the bytes sent during this snapshot
   *
   * @return number of bytes
   */
  public double getSend() {
    return send;
  }

  /**
   * Gets the bytes received during this snapshot
   *
   * @return number of bytes
   */
  public double getReceive() {
    return receive;
  }

  /**
   * Gets the total network capacity over active network interfaces in bits per second.
   * A network interface is considered inactive after five minutes of inactivity.
   *
   * @return speed in bits per second
   */
  public long getThroughput() {
    return throughput;
  }

  @Override
  public String toString() {
    return "NetworkData{" +
      "send=" + send +
      ", receive=" + receive +
      ", throughput=" + throughput +
      ", deltaMillis=" + deltaMillis +
      ", epochMillisTime=" + epochMillisTime +
      '}';
  }

  @Override
  public List<Metric> toMetricRecords() {
    return null;
  }
}
