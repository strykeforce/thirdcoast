package org.strykeforce.thirdcoast.telemetry.grapher;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import javax.inject.Inject;
import javax.inject.Named;
import okio.Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Handles data streaming with Grapher client. */
public class ClientHandler {

  static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
  final DatagramSocket socket;
  private final int port;
  ScheduledExecutorService scheduler;
  private SocketAddress socketAddress;
  private int count = 0;

  @Inject
  ClientHandler(@Named("client") int port, DatagramSocket socket) {
    this.port = port;
    this.socket = socket;
  }

  /**
   * Start streaming the items specified in the subscription.
   *
   * @param subscription Items to stream to client
   */
  public void start(Subscription subscription) {
    if (scheduler != null) {
      return;
    }
    logger.info("Sending graph data to {}:{}", subscription.client(), port);
    socketAddress = new InetSocketAddress(subscription.client(), port);
    scheduler = Executors.newSingleThreadScheduledExecutor();
    // FIXME: future not checked for exception
    ScheduledFuture<?> future =
        scheduler.scheduleAtFixedRate(
            () -> {
              Buffer buffer = new Buffer();
              try {
                subscription.measurementsToJson(buffer);
                byte[] bytes = buffer.readByteArray();
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length, socketAddress);
                socket.send(packet);
              } catch (IOException e) {
                logger.error("Exception sending grapher data", e);
              }
            },
            0,
            5,
            MILLISECONDS);
  }

  /** Stop streaming to client. */
  public void shutdown() {
    logger.info("Stopping graph data");
    if (scheduler != null) {
      scheduler.shutdown();
    }
    scheduler = null;
  }
}
