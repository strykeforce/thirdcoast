package org.strykeforce.thirdcoast.telemetry;

import com.ctre.CANTalon;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
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
  final List<Item> items = new ArrayList<>(16);
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
   * Un-register all Talons.
   */
  public void clear() {
    checkNotStarted();
    items.clear();
    logger.info("talon set was cleared");
  }

  /**
   * Register a Talon for telemetry sending and set its CAN bus frame rates to default values.
   *
   * @param talon the CANTalon to register for data collection
   * @see org.strykeforce.thirdcoast.talon.StatusFrameRate
   */
  public void register(CANTalon talon) {
    checkNotStarted();
    items.add(new TalonItem(talon));
    StatusFrameRate.DEFAULT.configure(talon);
    logger.info("registered talon {} with {}", talon.getDeviceID(), StatusFrameRate.DEFAULT);
  }

  /**
   * Registers an Item for telemetry sending.
   *
   * @param item the Item to register for data collection
   */
  public void register(Item item) {
    checkNotStarted();
    items.add(item);
    logger.info("registered item {}", item.description());
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
      throw new IllegalArgumentException("Talon with id " + talonId + " not found");
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
