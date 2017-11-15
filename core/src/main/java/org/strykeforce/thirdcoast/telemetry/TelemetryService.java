package org.strykeforce.thirdcoast.telemetry;

import com.ctre.CANTalon;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.StatusFrameRate;
import org.strykeforce.thirdcoast.telemetry.item.Item;
import org.strykeforce.thirdcoast.telemetry.item.TalonItem;

/**
 * The Telemetry service registers {@link Item} instances for data collection and controls the
 * starting and stopping of the service. When active, the services listens for incoming control
 * messages via a HTTP REST service and sends data over UDP.
 */
@Singleton
public class TelemetryService {

  final static Logger logger = LoggerFactory.getLogger(TelemetryService.class);

  // current implementation passes this list to the inventory as a collection via component binding
  // when start is called. The inventory copies this collection into a List, using its index in
  // this list as the inventory id.

  final Set<Item> items = new LinkedHashSet<>();
  TelemetryController telemetryController;
  boolean running = false;

  /**
   * Default constructor.
   */
  @Inject
  public TelemetryService() {
    logger.debug("Telemetry service created");
  }

  /**
   * Start the Telemetry service and listen for client connections.
   */
  public void start() {
    if (running) {
      logger.info("already started");
      return;
    }
    TelemetryComponent component = DaggerTelemetryComponent.builder().items(items).build();
    telemetryController = component.telemetryController();
    telemetryController.start();
    logger.info("started");
    running = true;
  }

  /**
   * Stop the Telemetry service.
   */
  public void stop() {
    if (!running) {
      logger.info("already stopped");
      return;
    }
    telemetryController.shutdown();
    telemetryController = null;
    logger.info("stopped");
    running = false;
  }

  /**
   * Un-register all Items.
   */
  public void clear() {
    checkNotStarted();
    items.clear();
    logger.info("item set was cleared");
  }

  /**
   * Register a Talon for telemetry sending and set its CAN bus frame rates to default values. If
   * this Talon is already registered the frame rates are not updated.
   *
   * @param talon the CANTalon to register for data collection
   * @see org.strykeforce.thirdcoast.talon.StatusFrameRate
   */
  public void register(CANTalon talon) {
    checkNotStarted();
    if (items.add(new TalonItem(talon))) {
      StatusFrameRate.DEFAULT.configure(talon);
      logger.info("registered talon {} with {}", talon.getDeviceID(), StatusFrameRate.DEFAULT);
      return;
    }
    logger.info("talon {} was already registered, did not reconfigure status frame rate",
        talon.getDeviceID());
  }

  /**
   * Registers an Item for telemetry sending.
   *
   * @param item the Item to register for data collection
   */
  public void register(Item item) {
    checkNotStarted();
    if (items.add(item)) {
      logger.info("registered item {}", item.description());
      return;
    }
    logger.info("item {} was already registered", item.description());

  }

  /**
   * Register a collection for telemetry sending.
   *
   * @param collection the collection of Items to register for data collection
   */
  public void registerAll(Collection<Item> collection) {
    checkNotStarted();
    items.addAll(collection);
    logger.info("registered all: {}", collection);
  }

  /**
   * Configure the Talon with the given ID with the given status frame rates.
   *
   * @param talonId the Talon to registerWith
   * @param rates the status frame rates
   */
  public void configureStatusFrameRates(int talonId, StatusFrameRate rates) {
    assert rates != null;

    if (running) {
      logger.warn("setting status frame rates while telemetry service is running");
    }

    Optional<Item> item = items.stream().filter(it -> it instanceof TalonItem && it.id() == talonId)
        .findFirst();

    if (!item.isPresent()) {
      throw new IllegalArgumentException("talon with id " + talonId + " not found");
    }

    TalonItem talonItem = (TalonItem) item.get();
    logger.info("setting talon {} ({}) to {}", talonItem.description(),
        talonItem.getTalon().getDeviceID(), rates);
    rates.configure(talonItem.getTalon());
  }

  private void checkNotStarted() {
    if (running) {
      throw new IllegalStateException("TelemetryService must be stopped.");
    }
  }

}
