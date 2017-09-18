package org.strykeforce.thirdcoast.telemetry;

import com.jsoniter.output.JsonStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

class Client implements Runnable {

  private final static Collection<Client> activeClients = new ConcurrentLinkedQueue<>();
  private final Socket socket;
  private volatile boolean shutdown = false;
  private boolean enabled = false;
  private int count = 0;

  Client(Socket socket) {
    this.socket = socket;
  }

  static void shutdownAll() {
    for (Client c : activeClients) {
      c.shutdown();
    }
  }

  void start() {
    new Thread(this).start();
  }

  void shutdown() {
    shutdown = true;
  }

  @Override
  public void run() {
    activeClients.add(this);
    try {
      JsonStream out = new JsonStream(socket.getOutputStream(), 1024);
      InputStream in = socket.getInputStream();
      checkCommand(in);
      while (!shutdown) {
        if (enabled) {
          out.writeVal(GraphDataMessage.sineWaves());
          out.write(Character.LINE_SEPARATOR);
          out.flush();
        }
        Thread.sleep(10);
        checkCommand(in);
      }
      out.close();
      socket.close();
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    } finally {
      activeClients.remove(this);
    }
  }

  private void checkCommand(InputStream in) {
    enabled = count++ < 2;
//    shutdown = count == 2;
  }
}
