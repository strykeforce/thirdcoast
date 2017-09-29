package org.strykeforce.thirdcoast.telemetry;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import org.strykeforce.thirdcoast.telemetry.message.Message;
import org.strykeforce.thirdcoast.telemetry.message.MessageFactory;

class Server implements Runnable {

  private final static int BUFSZ = 256;

  private final MessageFactory messageFactory;
  private final ClientFactory clientFactory;
  private Client client;
  private DatagramSocket socket;
  private byte[] buf = new byte[BUFSZ];
  private DatagramPacket packet = new DatagramPacket(buf, BUFSZ);
  private ShutdownNotifier shutdownNotifier = () -> false;

  Server(DatagramSocket socket, ClientFactory clientFactory, MessageFactory messageFactory) {
    assert socket != null;
    assert clientFactory != null;
    assert messageFactory != null;
    this.socket = socket;
    this.clientFactory = clientFactory;
    this.messageFactory = messageFactory;
  }

  @SuppressWarnings("InfiniteLoopStatement")
  @Override
  public void run() {
    try {
      while (!shutdownNotifier.shouldShutdown()) {
        socket.receive(packet);
        // FIXME: handle non-JSON packet
        Message message = messageFactory.createMessage(packet);
        switch (message.getType()) {
          case "refresh":
            break;
          case "subscribe":
            Client client = clientFactory.createClient();
            client.start();
        }
      }
    } catch (SocketException e) {
      System.out.println(e.getMessage());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  void start() {
    Thread thread = new Thread(this);
    thread.setDaemon(true);
    thread.start();
  }

  void shutdown() {
    shutdownNotifier = () -> true;
    if (socket != null) {
      socket.close();
    }
  }

  void setShutdownNotifier(ShutdownNotifier shutdownNotifier) {
    this.shutdownNotifier = shutdownNotifier;
  }
}
