package org.strykeforce.thirdcoast.telemetry;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.function.BooleanSupplier;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.strykeforce.thirdcoast.telemetry.message.Message;
import org.strykeforce.thirdcoast.telemetry.message.MessageParser;

class Server implements Runnable {

  private final static int BUFSZ = 256;

  private final MessageParser messageParser;
  private final ClientHandler clientHandler;
  private final DatagramSocket socket;
  private byte[] buf = new byte[BUFSZ];
  private DatagramPacket packet = new DatagramPacket(buf, BUFSZ);
  private BooleanSupplier shutdownNotifier = () -> false;

  @Inject
  Server(DatagramSocket socket, ClientHandler clientHandler, MessageParser messageParser) {
    this.socket = socket;
    this.clientHandler = clientHandler;
    this.messageParser = messageParser;
  }

  @Override
  public void run() {
    try {
      while (!shutdownNotifier.getAsBoolean()) {
        socket.receive(packet);
        // FIXME: handle non-JSON packet
        Message message = messageParser.parse(packet);
        switch (message.getType()) {
          case "refresh":
            break;
          case "subscribe":
            clientHandler.start();
            break;
        }
      }
    } catch (SocketException e) {
      System.out.println(e.getMessage());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void start() {
    Thread thread = new Thread(this);
    thread.setDaemon(true);
    thread.start();
  }

  public void shutdown() {
    shutdownNotifier = () -> true;
    if (socket != null) {
      socket.close();
    }
  }

  void setShutdownNotifier(BooleanSupplier shutdownNotifier) {
    this.shutdownNotifier = shutdownNotifier;
  }
}
