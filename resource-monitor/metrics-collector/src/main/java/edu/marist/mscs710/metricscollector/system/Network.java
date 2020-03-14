package edu.marist.mscs710.metricscollector.system;

import edu.marist.mscs710.metricscollector.data.NetworkData;
import oshi.SystemInfo;
import oshi.hardware.NetworkIF;

import java.time.Instant;

public class Network {
  private static final int FIVE_MINUTES_IN_MILLIS = 300000;
  private NetworkIF[] networks;
  private LastNetworkValues[] lastValues;
  private long lastCheckInMillis;

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

  public NetworkData getNetworkStatsSinceLastCheck() {
    long[] netStats = {0L, 0L, 0L, 0L};

    updateNetworkIFs();

    long curMillis = Instant.now().toEpochMilli();
    netStats[3] = curMillis - lastCheckInMillis;
    lastCheckInMillis = curMillis;

    for (int i = 0; i < networks.length; i++) {
      if (lastValues[i].millisIdle < FIVE_MINUTES_IN_MILLIS) {
        if (networks[i].getBytesSent() != 0 || networks[i].getBytesRecv() != 0) {
          netStats[0] += networks[i].getBytesSent() - lastValues[i].bytesSent;
          netStats[1] += networks[i].getBytesRecv() - lastValues[i].bytesRecv;
          netStats[2] += networks[i].getSpeed();
        }
      }
    }

    updateLastValues();
    // {delta total bytes sent, delta total bytes recv, tot speed, delta millis}
    return new NetworkData(netStats[0], netStats[1], netStats[2], netStats[3]);
  }

  private void updateNetworkIFs() {
    for (NetworkIF network : networks) {
      network.updateAttributes();
    }
  }

  private void updateLastValues() {
    for (int i = 0; i < networks.length; i++) {
      long curBytesSent = networks[i].getBytesSent();
      long curBytesRecv = networks[i].getBytesRecv();

      if (curBytesSent == lastValues[i].bytesSent && curBytesRecv == lastValues[i].bytesRecv) {
        lastValues[i].millisIdle += Instant.now().toEpochMilli() - lastCheckInMillis;
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
