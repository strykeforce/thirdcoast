package org.strykeforce.thirdcoast.deadeye.rx;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;

public class RxUdp {

  static final int UDP_SIZE = 512;

  private final DatagramSocket socket;

  private RxUdp() {
    try {
      socket = new DatagramSocket();
    } catch (SocketException e) {
      throw new RuntimeException(e);
    }
  }

  public static Observer<byte[]> observerSendingTo(SocketAddress address) {
    return new UdpObserver(address);
  }

  public static Observable<DatagramPacket> observableReceivingFrom(int port) {
    return Observable.<DatagramPacket>create(
            emitter -> {
              DatagramSocket socket = new DatagramSocket(port);
              emitter.setCancellable(socket::close);
              while (!emitter.isDisposed()) {
                byte[] buf = new byte[UDP_SIZE];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                try {
                  socket.receive(packet);
                } catch (IOException ioe) {
                  if (socket.isClosed()) {
                    // socket close called, don't send exception to global exception handler
                    break;
                  } else {
                    emitter.tryOnError(ioe);
                    break;
                  }
                }
                emitter.onNext(packet);
              }
            })
        .subscribeOn(Schedulers.io());
  }

  public static DisposableObserver<DatagramPacket> observerSendingDatagramPacket() {

    return new DisposableObserver<DatagramPacket>() {
      private final Logger logger = LoggerFactory.getLogger(this.getClass());
      DatagramSocket socket;

      @Override
      protected void onStart() {
        try {
          logger.debug("initializing observerSendingDatagramPacket socket");
          socket = new DatagramSocket();
        } catch (Exception e) {
          logger.error("error initializing DatagramSocket", e);
        }
      }

      @Override
      public void onNext(DatagramPacket datagramPacket) {
        try {
          socket.send(datagramPacket);
        } catch (Exception e) {
          logger.error("error sending UDP packet", e);
        }
      }

      @Override
      public void onError(Throwable e) {
        logger.error("received error from upstream Observable", e);
      }

      @Override
      public void onComplete() {
        logger.warn("upstream Observable is completed");
      }
    };
  }

  private static class UdpObserver implements Observer<byte[]> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SocketAddress address;
    private DatagramSocket socket;
    private Disposable sub;

    UdpObserver(SocketAddress address) {
      this.address = address;
    }

    @Override
    public void onSubscribe(Disposable d) {
      sub = d;
      try {
        socket = new DatagramSocket();
      } catch (SocketException e) {
        logger.error("can't create UDP socket", e);
        sub.dispose();
      }
    }

    @Override
    public void onNext(byte[] buf) {
      DatagramPacket packet = new DatagramPacket(buf, buf.length, address);
      try {
        socket.send(packet);
      } catch (IOException e) {
        logger.error("can't send UDP packet", e);
        sub.dispose();
      }
    }

    @Override
    public void onError(Throwable e) {
      logger.error("onError called", e);
    }

    @Override
    public void onComplete() {
      logger.debug("onComplete called");
    }
  }
}
