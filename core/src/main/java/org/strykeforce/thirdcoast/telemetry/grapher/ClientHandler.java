package org.strykeforce.thirdcoast.telemetry.grapher;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import javax.inject.Named;
import okio.Buffer;

class ClientHandler {

  private final int port;
  DatagramSocket socket;
  ScheduledExecutorService scheduler;
  private SocketAddress socketAddress;
  private int count = 0;


  @Inject
  ClientHandler(@Named("client") int port, DatagramSocket socket) {
    this.port = port;
    this.socket = socket;
  }

  public void start(Subscription subscription) {
    if (scheduler != null) {
      return;
    }
    System.out.print("\nSending graph data to " + subscription.client() + ":" + port + "\n");
    socketAddress = new InetSocketAddress(subscription.client(), port);
    scheduler = Executors.newSingleThreadScheduledExecutor();
    scheduler.scheduleAtFixedRate(() -> {
      Buffer buffer = new Buffer();
      try {
        subscription.measurementsToJson(buffer);
        byte[] bytes = buffer.readByteArray();
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, socketAddress);
        socket.send(packet);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }, 0, 5, MILLISECONDS);
  }

  public void shutdown() {
    System.out.print("Stopping graph data\n\n");
    if (scheduler != null) {
      scheduler.shutdown();
    }
    scheduler = null;
  }
}
