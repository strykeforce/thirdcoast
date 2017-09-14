package org.strykeforce.thirdcoast.telemetry;

import com.jsoniter.output.JsonStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

class Client implements Runnable {

  private final static Collection<Client> activeClients = new ConcurrentLinkedQueue<>();
  private final Socket socket;
  private volatile boolean stop = false;

  Client(Socket socket) {
    this.socket = socket;
  }

  static void stopAll() {
    for (Client c : activeClients) {
      c.stop = true;
    }
  }

  void start() {
    new Thread(this).start();
  }

  @Override
  public void run() {
    activeClients.add(this);
    try {
      JsonStream out = new JsonStream(socket.getOutputStream(), 1024);
      while (!stop) {
        out.writeVal(GraphDataMessage.sineWaves());
        out.flush();
        Thread.sleep(10);
      }
      out.close();
      socket.close();
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    } finally {
      activeClients.remove(this);
    }
  }
}
