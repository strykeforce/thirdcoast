package org.strykeforce.thirdcoast.telemetry;

import com.ctre.CANTalon;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.strykeforce.thirdcoast.telemetry.message.MessageFactory;

/**
 * The Telemetry service.
 */
public class Telemetry {

  private final Set<CANTalon> talons = new CopyOnWriteArraySet<>();
  private Server server;
  private ExecutorService serverExecutor;

  public Telemetry() {
    try {
      server = new Server(new DatagramSocket(5555), new ClientFactory(),
          new MessageFactory());
    } catch (SocketException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    Telemetry telemetry = new Telemetry();
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
}
