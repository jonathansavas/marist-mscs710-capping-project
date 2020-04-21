package edu.marist.mscs710.metricscollector.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.marist.mscs710.metricscollector.metric.Fields;

import java.util.List;

import static edu.marist.mscs710.metricscollector.utils.DataUtils.weightedAverage;

/**
 * Holds a snapshot of Network data.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
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
  private double send;

  @JsonProperty(Fields.NETWORK_RECEIVE)
  private double receive;

  @JsonProperty(Fields.NETWORK_THROUGHPUT)
  private long throughput;

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
   * Constructs a new <tt>NetworkData</tt> with the supplied metrics.
   *
   * @param send            kilobits per second sent rate during this snapshot
   * @param receive         kilobits per second received rate during this snapshot
   * @param throughput      total kilobits per second network capacity over active networks
   * @param deltaMillis     time covered by this snapshot
   * @param epochMillisTime epoch milli timestamp of this snapshot
   */
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
   * Gets the kilobits per second send rate.
   *
   * @return network send rate
   */
  public double getSend() {
    return send;
  }

  /**
   * Gets the kilobits per second receive rate.
   *
   * @return network receive rate
   */
  public double getReceive() {
    return receive;
  }

  /**
   * Gets the total network capacity over active network interfaces in kilobits per second.
   * A network interface is considered inactive after five minutes of inactivity.
   *
   * @return total network throughput
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    NetworkData that = (NetworkData) o;
    return Double.compare(that.send, send) == 0 &&
      Double.compare(that.receive, receive) == 0 &&
      throughput == that.throughput&&
      deltaMillis == that.deltaMillis &&
      epochMillisTime == that.epochMillisTime;
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

  /**
   * Combines a list of <tt>NetworkData</tt> into a single instance. This method
   * takes a weighted average of all fields based on <tt>deltaMillis</tt>.
   *
   * @param metrics list of Network metrics
   * @return an aggregate <tt>NetworkData</tt> instance
   */
  public static NetworkData combine(List<NetworkData> metrics) {
    double datetime = 0;
    long totalMillis = 0;
    double send = 0.0;
    double receive = 0.0;
    double throughput = 0;

    for (NetworkData data : metrics) {
      long deltaMillis = data.getDeltaMillis();
      datetime = weightedAverage(datetime, totalMillis, data.getEpochMillisTime(), deltaMillis);
      send = weightedAverage(send, totalMillis, data.getSend(), deltaMillis);
      receive = weightedAverage(receive, totalMillis, data.getReceive(), deltaMillis);
      throughput = weightedAverage(throughput, totalMillis, data.getThroughput(), deltaMillis);
      totalMillis += deltaMillis;
    }

    return new NetworkData(send, receive, (long) throughput, totalMillis, (long) datetime);
  }
}
