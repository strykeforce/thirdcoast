package org.strykeforce.thirdcoast.telemetry;

import com.ctre.CANTalon;
import dagger.BindsInstance;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
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

  private final Set<CANTalon> talons = new CopyOnWriteArraySet<>();
  private final Server server;

  @Inject
  TelemetryService(Server server) {
    this.server = server;
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
    server.start();
  }

  /**
   * Stop the Telemetry service.
   */
  public void stop() {
    server.shutdown();
  }

  /**
   * Register a Talon for telemetry sending. This is thread-safe.
   *
   * @param talon the CANTalon to add
   */
  public void register(CANTalon talon) {
    talons.add(talon);
  }

  /**
   * Register a collection for telemetry sending. This is thread-safe.
   *
   * @param collection the collection of CANTalons to add
   */
  public void registerAll(Collection<CANTalon> collection) {
    talons.addAll(collection);
  }

  /**
   * Returns an unmodifiable view of the registered Talons.
   *
   * @return a unmodifiable Set containing registered CANTalons
   */
  public Set<CANTalon> getTalons() {
    return Collections.unmodifiableSet(talons);
  }

  @Override
  public String toString() {
    return "Telemetry{" +
        "talons=" + talons +
        '}';
  }

  @Singleton
  @dagger.Component(modules = {DatagramModule.class})
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
