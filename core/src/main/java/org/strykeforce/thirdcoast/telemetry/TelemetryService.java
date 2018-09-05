package org.strykeforce.thirdcoast.telemetry;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.config.StatusFrameRate;
import org.strykeforce.thirdcoast.telemetry.item.Item;
import org.strykeforce.thirdcoast.telemetry.item.TalonItem;

/**
 * The Telemetry service registers {@link Item} instances for data collection and controls the
 * starting and stopping of the service. When active, the services listens for incoming config
 * messages via a HTTP REST service and sends data over UDP.
 */
@Singleton
public class TelemetryService {

  static final Logger logger = LoggerFactory.getLogger(TelemetryService.class);

  // current implementation passes this list to the inventory as a collection via component binding
  // when start is called. The inventory copies this collection into a List, using its index in
  // this list as the inventory id.

  private final Set<Item> items = new LinkedHashSet<>();
  private final TelemetryControllerFactory telemetryControllerFactory;
  private TelemetryController telemetryController;
  private boolean running = false;

  /**
   * Default constructor.
   *
   * @param telemetryControllerFactory telemetry controller factory
   */
  @Inject
  public TelemetryService(TelemetryControllerFactory telemetryControllerFactory) {
    this.telemetryControllerFactory = telemetryControllerFactory;
    logger.debug("Telemetry service created");
  }

  /** Start the Telemetry service and listen for client connections. */
  public void start() {
    if (running) {
      logger.info("already started");
      return;
    }
    telemetryController = telemetryControllerFactory.create(new RobotInventory(items));
    telemetryController.start();
    logger.info("started");
    running = true;
  }

  /** Stop the Telemetry service. */
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
   *
   * @throws IllegalStateException if TelemetryService is running.
   */
  public void clear() {
    checkNotStarted();
    items.clear();
    logger.info("item set was cleared");
  }

  /**
   * Convenience method to register a TalonSRX for telemetry sending.
   *
   * @param talon the TalonSRX to register for data collection
   * @throws IllegalStateException if TelemetryService is running.
   * @see StatusFrameRate
   */
  public void register(TalonSRX talon) {
    register(new TalonItem(talon));
  }

  /**
   * Registers an Item for telemetry sending.
   *
   * @param item the Item to register for data collection
   * @throws IllegalStateException if TelemetryService is running.
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
   * @throws IllegalStateException if TelemetryService is running.
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
  @Deprecated
  public void configureStatusFrameRates(int talonId, StatusFrameRate rates) {
    assert rates != null;

    if (running) {
      logger.warn("setting status frame rates while telemetry service is running");
    }

    Optional<Item> item =
        items
            .stream()
            .filter(it -> it instanceof TalonItem && it.deviceId() == talonId)
            .findFirst();

    if (!item.isPresent()) {
      throw new IllegalArgumentException("talon with id " + talonId + " not found");
    }

    TalonItem talonItem = (TalonItem) item.get();
    logger.info(
        "setting talon {} ({}) to {}",
        talonItem.description(),
        talonItem.getTalon().getDeviceID(),
        rates);
    rates.configure(talonItem.getTalon());
  }

  /**
   * Get an unmodifiable view of the registered items.
   *
   * @return an unmodifiable Set of Items.
   */
  public Set<Item> getItems() {
    return Collections.unmodifiableSet(items);
  }

  /**
   * Unregister an {@code Item} from a stopped {@code TelemetryService}.
   *
   * @param item the Item to remove.
   * @throws AssertionError if TelemetryService is running.
   */
  public void remove(Item item) {
    checkNotStarted();
    if (items.remove(item)) {
      logger.info("removed {}", item);
      return;
    }
    throw new AssertionError(item.toString());
  }

  private void checkNotStarted() {
    if (running) {
      throw new IllegalStateException("TelemetryService must be stopped.");
    }
  }
}
