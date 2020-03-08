package edu.marist.mscs710.metricscollector;

import oshi.SystemInfo;
import oshi.hardware.NetworkIF;

import java.time.Instant;

public class Network {
  private static final int FIVE_MINUTES_IN_SEC = 300;
  private NetworkIF[] networks;
  private LastNetworkValues[] lastValues;
  private long lastCheckInSeconds;

  public Network() {
    this.networks = new SystemInfo().getHardware().getNetworkIFs();
    lastCheckInSeconds = Instant.now().getEpochSecond();
    this.lastValues = new LastNetworkValues[networks.length];

    for (int i = 0; i < networks.length; i++) {
      lastValues[i] = new LastNetworkValues();
      lastValues[i].bytesSent = networks[i].getBytesSent();
      lastValues[i].bytesRecv = networks[i].getBytesRecv();
      lastValues[i].secondsIdle = 0;
    }
  }

  public long[] getNetworkStatsSinceLastCheck() {
    long[] networkStats = {0L, 0L, 0L, 0L};

    updateNetworkIFs();

    long curSeconds = Instant.now().getEpochSecond();
    networkStats[3] = curSeconds - lastCheckInSeconds;
    lastCheckInSeconds = curSeconds;

    for (int i = 0; i < networks.length; i++) {
      if (lastValues[i].secondsIdle < FIVE_MINUTES_IN_SEC) {
        if (networks[i].getBytesSent() != 0 || networks[i].getBytesRecv() != 0) {
          networkStats[0] += networks[i].getBytesSent() - lastValues[i].bytesSent;
          networkStats[1] += networks[i].getBytesRecv() - lastValues[i].bytesRecv;
          networkStats[2] += networks[i].getSpeed();
        }
      }
    }

    updateLastValues();
    // {delta total bytes sent, delta total bytes recv, tot speed, delta seconds}
    return networkStats;
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
        lastValues[i].secondsIdle += Instant.now().getEpochSecond() - lastCheckInSeconds;
      } else {
        lastValues[i].secondsIdle = 0;
        lastValues[i].bytesSent = curBytesSent;
        lastValues[i].bytesRecv = curBytesRecv;
      }
    }
  }
}

class LastNetworkValues {
  long bytesSent;
  long bytesRecv;
  long secondsIdle;
}
