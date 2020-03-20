package edu.marist.mscs710.metricscollector.data;

public class NetworkData extends MetricData {
  private long bytesSent; // During delta millis
  private long bytesRecv; // During delta millis
  private long speed; // Bits per second, sum over active network interfaces

  public NetworkData(long bytesSent, long bytesRecv, long speed, long deltaMillis, long epochMillisTime) {
    this.bytesSent = bytesSent;
    this.bytesRecv = bytesRecv;
    this.speed = speed;
    this.deltaMillis = deltaMillis;
    this.epochMillisTime = epochMillisTime;
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
}
