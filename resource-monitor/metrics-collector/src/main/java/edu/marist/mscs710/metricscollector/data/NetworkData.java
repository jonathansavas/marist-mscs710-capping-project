package edu.marist.mscs710.metricscollector.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.marist.mscs710.metricscollector.metric.Fields;

import java.util.List;

import static edu.marist.mscs710.metricscollector.utils.DataUtils.weightedAverage;

/**
 * Holds a snapshot of Network data.
 */
public class NetworkData extends MetricData {
  public static final String SQL_INSERT_PREFIX = "INSERT INTO " +
    Fields.METRIC_TYPE_NETWORK + " (" +
    Fields.NETWORK_DATETIME + ',' +
    Fields.NETWORK_DELTA_MILLIS + ',' +
    Fields.NETWORK_RECEIVE + ',' +
    Fields.NETWORK_SEND + ',' +
    Fields.NETWORK_THROUGHPUT + ") VALUES ";

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

  @JsonCreator
  public NetworkData(@JsonProperty(Fields.NETWORK_SEND)double send,
                     @JsonProperty(Fields.NETWORK_RECEIVE) double receive,
                     @JsonProperty(Fields.NETWORK_THROUGHPUT) long throughput,
                     @JsonProperty(Fields.DELTA_MILLIS)long deltaMillis,
                     @JsonProperty(Fields.DATETIME) long epochMillisTime) {
    this.send = send;
    this.receive = receive;
    this.throughput = throughput;
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
  public String toSqlInsertString() {
    return SQL_INSERT_PREFIX + '(' +
      epochMillisTime + ',' +
      deltaMillis + ',' +
      receive + ',' +
      send + ',' +
      throughput + ')' + ';';
  }

  public static NetworkData combine(List<NetworkData> metrics) {
    long datetime = 0;
    long totalMillis = 0;
    double send = 0.0;
    double receive = 0.0;
    long throughput = 0;

    for (NetworkData data : metrics) {
      long deltaMillis = data.getDeltaMillis();
      datetime = weightedAverage(datetime, totalMillis, data.getEpochMillisTime(), deltaMillis);
      send = weightedAverage(send, totalMillis, data.getSend(), deltaMillis);
      receive = weightedAverage(receive, totalMillis, data.getReceive(), deltaMillis);
      throughput = weightedAverage(throughput, totalMillis, data.getThroughput(), deltaMillis);
      totalMillis += deltaMillis;
    }

    return new NetworkData(send, receive, throughput, totalMillis, datetime);
  }
}
