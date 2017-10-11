package org.strykeforce.thirdcoast.telemetry.grapher;

import dagger.Module;
import dagger.Provides;
import java.net.DatagramSocket;
import java.net.SocketException;
import javax.inject.Named;

@Module
public class NetworkModule {

  @Provides
  @Named("server")
  static int provideServerPort() {
    return 5800;
  }

  @Provides
  @Named("client")
  static int provideClientPort() {
    return 5555;
  }

  @Provides
  static DatagramSocket provideDatagramSocket() {
    try {
      return new DatagramSocket();
    } catch (SocketException e) {
      e.printStackTrace();
    }
    return null;
  }

}
