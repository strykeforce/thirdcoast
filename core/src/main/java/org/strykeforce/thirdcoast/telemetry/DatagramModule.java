package org.strykeforce.thirdcoast.telemetry;

import dagger.Module;
import dagger.Provides;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;

@Module
class DatagramModule {

  @Provides
  static DatagramSocket provideDatagramSocket(SocketAddress socketAddress) {
    try {
      return new DatagramSocket(socketAddress);
    } catch (SocketException e) {
      e.printStackTrace();
    }
    return null;
  }

}
