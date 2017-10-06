package org.strykeforce.thirdcoast.telemetry;

import java.net.DatagramSocket;
import javax.inject.Inject;
import javax.inject.Singleton;

class ClientHandler implements Runnable {


  private int count = 0;

  @Inject
  ClientHandler() {
  }


  public void start() {
    Thread thread = new Thread(this);
    thread.setDaemon(true);
    thread.start();
  }

  public void shutdown() {

  }

  @Override
  public void run() {
    try {
    } finally {
    }
  }
}

//      JsonStream out = new JsonStream(socket.getOutputStream(), 1024);
//      InputStream in = socket.getInputStream();
//      checkCommand(in);
//      while (!shutdown) {
//        if (enabled) {
//          out.writeVal(GraphDataMessage.sineWaves());
//          out.write(Character.LINE_SEPARATOR);
//          out.flush();
//        }
//        Thread.sleep(10);
//        checkCommand(in);
//      }
//      out.close();
//      socket.close();
//    } catch (IOException | InterruptedException e) {
//      e.printStackTrace();
