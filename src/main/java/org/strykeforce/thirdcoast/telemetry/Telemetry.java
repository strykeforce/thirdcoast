package org.strykeforce.thirdcoast.telemetry;

import com.ctre.CANTalon;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Telemetry {

  private final static Telemetry instance = new Telemetry();
  private final Set<CANTalon> talons = new CopyOnWriteArraySet<>();
  private Server server = new Server();
  private ExecutorService serverExecutor;

  private Telemetry() {

  }

  /**
   * Get the Telemetry service.
   *
   * @return the telemetry singleton
   */
  public static Telemetry getInstance() {
    return instance;
  }

  public static void main(String[] args) {
    ScheduledExecutorService s = Executors.newSingleThreadScheduledExecutor();
    s.schedule(() -> {
      Telemetry.getInstance().stop();
    }, 120, TimeUnit.SECONDS);
    s.shutdown();
    Telemetry.getInstance().start();
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
    server.stop();
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


}
