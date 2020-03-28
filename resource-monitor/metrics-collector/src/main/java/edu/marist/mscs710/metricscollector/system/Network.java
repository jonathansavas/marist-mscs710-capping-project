package edu.marist.mscs710.metricscollector.system;

import edu.marist.mscs710.metricscollector.MetricSource;
import edu.marist.mscs710.metricscollector.data.NetworkData;
import oshi.SystemInfo;
import oshi.hardware.NetworkIF;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * Represents the network interfaces of a system. Produces metrics on demand,
 * keeping the previous state of the interfaces.
 */
public class Network implements MetricSource {
  private static final int FIVE_MINUTES_IN_MILLIS = 300000;
  private NetworkIF[] networks;
  private LastNetworkValues[] lastValues;
  private long lastCheckInMillis;

  /**
   * Constructs a new <tt>Network</tt>
   */
  public Network() {
    this.networks = new SystemInfo().getHardware().getNetworkIFs();
    lastCheckInMillis = Instant.now().toEpochMilli();
    this.lastValues = new LastNetworkValues[networks.length];

    for (int i = 0; i < networks.length; i++) {
      lastValues[i] = new LastNetworkValues();
      lastValues[i].bytesSent = networks[i].getBytesSent();
      lastValues[i].bytesRecv = networks[i].getBytesRecv();
      lastValues[i].millisIdle = 0;
    }
  }

  @Override
  public List<NetworkData> getMetricData() {
    long deltaBytesSent = 0;
    long deltaBytesRecv = 0;
    long totalActiveSpeed = 0;

    updateNetworkIFs();

    long curMillis = Instant.now().toEpochMilli();
    long deltaMillis = curMillis - lastCheckInMillis;
    lastCheckInMillis = curMillis;

    for (int i = 0; i < networks.length; i++) {
      if (lastValues[i].millisIdle < FIVE_MINUTES_IN_MILLIS) {
        if (networks[i].getBytesSent() != 0 || networks[i].getBytesRecv() != 0) {
          deltaBytesSent += networks[i].getBytesSent() - lastValues[i].bytesSent;
          deltaBytesRecv += networks[i].getBytesRecv() - lastValues[i].bytesRecv;
          totalActiveSpeed += networks[i].getSpeed();
        }
      }
    }

    updateLastValues(deltaMillis);

    return Collections.singletonList(
      new NetworkData(deltaBytesSent, deltaBytesRecv, totalActiveSpeed, deltaMillis, curMillis)
    );
  }

  private void updateNetworkIFs() {
    for (NetworkIF network : networks) {
      network.updateAttributes();
    }
  }

  private void updateLastValues(long deltaMillis) {
    for (int i = 0; i < networks.length; i++) {
      long curBytesSent = networks[i].getBytesSent();
      long curBytesRecv = networks[i].getBytesRecv();

      if (curBytesSent == lastValues[i].bytesSent && curBytesRecv == lastValues[i].bytesRecv) {
        lastValues[i].millisIdle += deltaMillis;
      } else {
        lastValues[i].millisIdle = 0;
        lastValues[i].bytesSent = curBytesSent;
        lastValues[i].bytesRecv = curBytesRecv;
      }
    }
  }

  private static class LastNetworkValues {
    long bytesSent;
    long bytesRecv;
    long millisIdle;
  }
}
