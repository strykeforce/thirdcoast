package org.strykeforce.thirdcoast.deadeye;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.strykeforce.thirdcoast.deadeye.ConnectionEvent.CONNECTED;
import static org.strykeforce.thirdcoast.deadeye.ConnectionEvent.DISCONNECTED;
import static org.strykeforce.thirdcoast.deadeye.DeadeyeMessage.Type.HEARTBEAT;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Timed;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.deadeye.rx.RxUdp;

public class DeadeyeService {

  private static final int PING_INTERVAL = 100;
  private static final int PONG_LIMIT = PING_INTERVAL * 4;
  private static final int PORT = 5555;

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  // this IP address is hardcoded for tethering in Android 6.0 Marshmallow
  private final InetSocketAddress ADDRESS = new InetSocketAddress("192.168.42.129", PORT);

  private final Observable<Timed<DeadeyeMessage>> pongs;
  private final Observable<DeadeyeMessage> messageObservable;
  private final Observable<Timed<Long>> heartbeat;
  private final Observable<ConnectionEvent> connectionEventObservable;
  //  private final Disposable disposable;

  private Disposable connectionEventDisposable;

  public DeadeyeService() {
    logger.info("starting pings to {}:{} every {} ms", ADDRESS.getHostName(), PORT, PING_INTERVAL);

    // send pings
    Observable.interval(PING_INTERVAL, MILLISECONDS)
        .map(i -> DeadeyeMessage.HEARTBEAT_BYTES)
        .subscribe(RxUdp.observerSendingTo(ADDRESS));

    // monitor pongs
    logger.info("listening for pongs on port {} with limit {} ms.", PORT, PONG_LIMIT);

    // TODO: make defensive copy of byte[] in RxUDP, debug print sizeof(Data)
    messageObservable =
        RxUdp.observableReceivingFrom(PORT)
            .map(DatagramPacket::getData)
            .map(DeadeyeMessage::new)
            .share();

    pongs =
        messageObservable
            .filter(deadeyeMessage -> deadeyeMessage.type == HEARTBEAT)
            .timestamp(MILLISECONDS);

    heartbeat = Observable.interval(PING_INTERVAL / 2, MILLISECONDS).timestamp(MILLISECONDS);

    connectionEventObservable =
        Observable.combineLatest(pongs, heartbeat, (p, h) -> h.time() - p.time())
            .distinctUntilChanged(time -> time > PONG_LIMIT)
            .map(time -> time > PONG_LIMIT ? DISCONNECTED : CONNECTED)
            .startWith(DISCONNECTED)
            .share();
  }

  public void enableConnectionEventLogging(boolean enable) {
    if (connectionEventDisposable != null) connectionEventDisposable.dispose();

    if (enable) {
      connectionEventDisposable =
          connectionEventObservable
              .map(ConnectionEvent::toString)
              .subscribe(logger::info, t -> logger.error("connection event logging", t));
    }
  }

  public Observable<ConnectionEvent> getConnectionEventObservable() {
    return connectionEventObservable;
  }

  public Observable<DeadeyeMessage> getMessageObservable() {
    return messageObservable;
  }
}
