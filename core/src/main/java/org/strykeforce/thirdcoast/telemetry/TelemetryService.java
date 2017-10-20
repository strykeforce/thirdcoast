package org.strykeforce.thirdcoast.telemetry;

import com.ctre.CANTalon;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.strykeforce.thirdcoast.talon.StatusFrameRate;
import org.strykeforce.thirdcoast.telemetry.grapher.item.Item;
import org.strykeforce.thirdcoast.telemetry.grapher.item.TalonItem;

/**
 * The Telemetry service registers {@link Item} instances for data collection and controls the
 * starting and stopping of the service. When active, the services listens for incoming control
 * messages via a HTTP REST service and sends data over UDP.
 */
@Singleton
public class TelemetryService {

  TelemetryController telemetryController;
  Collection<Item> items = new ArrayList<>(16);

  /**
   * Default constructor.
   */
  @Inject
  public TelemetryService() {
  }

  /**
   * Start the Telemetry service and listen for client connections.
   */
  public void start() {
    TelemetryComponent component = DaggerTelemetryComponent.builder().items(items).build();
    telemetryController = component.telemetryController();
    telemetryController.start();

  }

  /**
   * Stop the Telemetry service.
   */
  public void stop() {
    telemetryController.shutdown();
  }

  /**
   * Register a Talon for telemetry sending and set its CAN bus frame rates to default values.
   *
   * @param talon the CANTalon to register for data collection
   * @see org.strykeforce.thirdcoast.talon.StatusFrameRate
   */
  public void register(CANTalon talon) {
    register(new TalonItem(talon));
    StatusFrameRate.DEFAULT.configure(talon);
  }

  /**
   * Registers an Item for telemetry sending.
   *
   * @param item the Item to register for data collection
   */
  public void register(Item item) {
    items.add(item);
  }

  /**
   * Register a collection for telemetry sending.
   *
   * @param collection the collection of Items to register for data collection
   */
  public void registerAll(Collection<Item> collection) {
    items.addAll(collection);
  }

  /**
   * Configure the Talon with the given ID with the given status frame rates.
   *
   * @param talonId the Talon to configure
   * @param rates the status frame rates
   */
  public void configureStatusFrameRates(int talonId, StatusFrameRate rates) {
    assert rates != null;

    Optional<Item> item = items.stream().filter(it -> {
      return it instanceof TalonItem && it.id() == talonId;
    }).findFirst();

    if (!item.isPresent()) {
      throw new IllegalArgumentException("Talon with id " + talonId + " not found");
    }

    TalonItem talonItem = (TalonItem)item.get();
    rates.configure(talonItem.getTalon());
  }

}
