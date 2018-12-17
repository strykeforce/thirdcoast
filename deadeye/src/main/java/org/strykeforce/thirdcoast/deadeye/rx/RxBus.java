package org.strykeforce.thirdcoast.deadeye.rx;

import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import javax.inject.Inject;
import javax.inject.Singleton;

// courtesy: https://github.com/kaushikgopal/RxJava-Android-Samples
@Singleton
public class RxBus {

  private final Relay<Object> bus = PublishRelay.create().toSerialized();

  @Inject
  public RxBus() {}

  public void send(Object o) {
    bus.accept(o);
  }

  public Flowable<Object> asFlowable() {
    return bus.toFlowable(BackpressureStrategy.LATEST);
  }

  public boolean hasObservers() {
    return bus.hasObservers();
  }
}
