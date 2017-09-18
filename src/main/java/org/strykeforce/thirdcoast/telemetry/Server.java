package org.strykeforce.thirdcoast.telemetry;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;

class Server implements Runnable {

  private ServerSocket serverSocket;

  @SuppressWarnings("InfiniteLoopStatement")
  @Override
  public void run() {
    try {
      serverSocket = new ServerSocket(5555);
      while (true) {
        new Client(serverSocket.accept()).start();
      }
    } catch (SocketException e) {
      System.out.println("server socket closed, shutting down...");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  void start() {
    new Thread(this).start();
  }

  void stop() {
    if (serverSocket == null) {
      return;
    }

    try {
      serverSocket.close();
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
    Client.shutdownAll();
  }

}
