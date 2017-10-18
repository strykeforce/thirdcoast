package org.strykeforce.thirdcoast.telemetry;

import com.ctre.CANTalon;
import java.util.ArrayList;
import java.util.Collection;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.strykeforce.thirdcoast.telemetry.grapher.GrapherController;

/**
 * The Telemetry service.
 */
@Singleton
public class TelemetryService {

  GrapherController grapherController;
  Collection<CANTalon> talons = new ArrayList<>(16);

  @Inject
  public TelemetryService() {
  }

  /**
   * Start the Telemetry service and listen for client connections.
   */
  public void start() {
    TelemetryComponent component = DaggerTelemetryComponent.builder().talons(talons).build();
    grapherController = component.grapherController();
    grapherController.start();

  }

  /**
   * Stop the Telemetry service.
   */
  public void stop() {
    grapherController.shutdown();
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

}
