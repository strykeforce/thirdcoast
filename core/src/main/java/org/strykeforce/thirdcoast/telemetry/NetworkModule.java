package org.strykeforce.thirdcoast.telemetry;

import dagger.Module;
import dagger.Provides;
import java.net.DatagramSocket;
import java.net.SocketException;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <a href="https://google.github.io/dagger/" target="_top">Dagger</a> dependency-injection support
 * for networking configuration.
 */
@Module
public abstract class NetworkModule {

  final static Logger logger = LoggerFactory.getLogger(NetworkModule.class);

  @Provides
  @Named("server")
  static int provideServerPort() {
    logger.debug("providing server port 5800");
    return 5800;
  }

  @Provides
  @Named("client")
  static int provideClientPort() {
    logger.debug("providing client port 5555");
    return 5555;
  }

  @Provides
  static DatagramSocket provideDatagramSocket() {
    try {
      return new DatagramSocket();
    } catch (SocketException e) {
      logger.error("Failed to create new socket", e);
    }
    throw new RuntimeException();
  }

}
