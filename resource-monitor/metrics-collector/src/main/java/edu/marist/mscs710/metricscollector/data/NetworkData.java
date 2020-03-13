package edu.marist.mscs710.metricscollector.data;

public class NetworkData extends MetricData {
  private long bytesSent;
  private long bytesRecv;
  private long speed; // Bits per second

  public NetworkData(long bytesSent, long bytesRecv, long speed, long deltaMillis) {
    this.bytesSent = bytesSent;
    this.bytesRecv = bytesRecv;
    this.speed = speed;
    this.deltaMillis = deltaMillis;
  }

  public long getBytesSent() {
    return bytesSent;
  }

  public long getBytesRecv() {
    return bytesRecv;
  }

  public long getSpeed() {
    return speed;
  }
}
