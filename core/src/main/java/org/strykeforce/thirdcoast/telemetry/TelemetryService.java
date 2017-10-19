package org.strykeforce.thirdcoast.telemetry;

import com.ctre.CANTalon;
import java.util.ArrayList;
import java.util.Collection;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.strykeforce.thirdcoast.telemetry.grapher.GrapherController;
import org.strykeforce.thirdcoast.telemetry.grapher.item.Item;
import org.strykeforce.thirdcoast.telemetry.grapher.item.TalonItem;

/**
 * The Telemetry service.
 */
@Singleton
public class TelemetryService {

  GrapherController grapherController;
  Collection<Item> items = new ArrayList<>(16);

  @Inject
  public TelemetryService() {
  }

  /**
   * Start the Telemetry service and listen for client connections.
   */
  public void start() {
    TelemetryComponent component = DaggerTelemetryComponent.builder().items(items).build();
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
   * Register a Talon for telemetry sending.
   *
   * @param talon the CANTalon to add
   */
  public void register(CANTalon talon) {
    register(new TalonItem(talon));
  }

  /**
   * Registers an Item for telemetry sending.
   *
   * @param item the Item to add
   */
  public void register(Item item) {
    items.add(item);
  }

  /**
   * Register a collection for telemetry sending.
   *
   * @param collection the collection of Items to add
   */
  public void registerAll(Collection<Item> collection) {
    items.addAll(collection);
  }

}
