package org.strykeforce.thirdcoast.telemetry;

import com.ctre.CANTalon;
import dagger.BindsInstance;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * The Telemetry service.
 */
@Singleton
public class TelemetryService {

  @Inject
  TelemetryService() {
  }

  public static void main(String[] args) {
    Component component = DaggerTelemetryService_Component.builder()
        .socketAddress(new InetSocketAddress(5555))
        .build();
    TelemetryService telemetry = component.getTelemetryService();
    ScheduledExecutorService s = Executors.newSingleThreadScheduledExecutor();
    s.schedule(() -> {
      telemetry.stop();
    }, 60, TimeUnit.SECONDS);
    s.shutdown();
    telemetry.start();
  }

  /**
   * Start the Telemetry service and listen for client connections.
   */
  public void start() {
//    server.start();
  }

  /**
   * Stop the Telemetry service.
   */
  public void stop() {
//    server.shutdown();
  }

  /**
   * Register a Talon for telemetry sending. This is thread-safe.
   *
   * @param talon the CANTalon to add
   */
  public void register(CANTalon talon) {
  }

  /**
   * Register a collection for telemetry sending. This is thread-safe.
   *
   * @param collection the collection of CANTalons to add
   */
  public void registerAll(Collection<CANTalon> collection) {

  }

  @Singleton
//  @dagger.Component(modules = {DatagramModule.class})
  @dagger.Component()
  interface Component {

    TelemetryService getTelemetryService();

    @dagger.Component.Builder
    interface Builder {

      Component build();

      @BindsInstance
      Builder socketAddress(SocketAddress address);
    }
  }
}
