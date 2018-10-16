package org.strykeforce.thirdcoast.telemetry

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import mu.KotlinLogging
import org.strykeforce.thirdcoast.swerve.SwerveDrive
import org.strykeforce.thirdcoast.talon.config.StatusFrameRate
import org.strykeforce.thirdcoast.telemetry.item.Item
import org.strykeforce.thirdcoast.telemetry.item.TalonItem
import java.util.*
import java.util.function.Function

private val logger = KotlinLogging.logger {}

/**
 * The Telemetry service registers [Item] instances for data collection and controls the
 * starting and stopping of the service. When active, the services listens for incoming config
 * messages via a HTTP REST service and sends data over UDP.
 */
class TelemetryService
/**
 * Default constructor.
 *
 * @param telemetryControllerFactory telemetry controller factory
 */
  (private val telemetryControllerFactory: Function<Inventory, TelemetryController>) {

  // current implementation passes this list to the inventory as a collection via component binding
  // when start is called. The inventory copies this collection into a List, using its index in
  // this list as the inventory id.

  private val items = LinkedHashSet<Item>()
  private var telemetryController: TelemetryController? = null
  private var running = false

  init {
    logger.debug("Telemetry service created")
  }

  /** Start the Telemetry service and listen for client connections.  */
  fun start() {
    if (running) {
      logger.info("already started")
      return
    }
    telemetryController = telemetryControllerFactory.apply(RobotInventory(items))
    telemetryController!!.start()
    logger.info("started")
    running = true
  }

  /** Stop the Telemetry service.  */
  fun stop() {
    if (!running) {
      logger.info("already stopped")
      return
    }
    telemetryController!!.shutdown()
    telemetryController = null
    logger.info("stopped")
    running = false
  }

  /**
   * Un-register all Items.
   *
   * @throws IllegalStateException if TelemetryService is running.
   */
  fun clear() {
    checkNotStarted()
    items.clear()
    logger.info("item set was cleared")
  }

  /**
   * Registers an Item for telemetry sending.
   *
   * @param item the Item to register for data collection
   * @throws IllegalStateException if TelemetryService is running.
   */
  fun register(item: Item) {
    checkNotStarted()
    if (items.add(item)) {
      logger.info("registered item {}", item.description())
      return
    }
    logger.info("item {} was already registered", item.description())
  }

  /**
   * Register a collection for telemetry sending.
   *
   * @param collection the collection of Items to register for data collection
   * @throws IllegalStateException if TelemetryService is running.
   */
  fun registerAll(collection: Collection<Item>) {
    checkNotStarted()
    items.addAll(collection)
    logger.info("registered all: {}", collection)
  }

  /**
   * Convenience method to register a `TalonSRX` for telemetry sending.
   *
   * @param talon the TalonSRX to register for data collection
   * @throws IllegalStateException if TelemetryService is running.
   * @see StatusFrameRate
   */
  fun register(talon: TalonSRX) {
    register(TalonItem(talon))
  }

  /**
   * Convenience method to register a `SwerveDrive` for telemetry sending.
   *
   * @param swerveDrive the SwerveDrive to register for data collection
   * @throws IllegalStateException if TelemetryService is running.
   * @see StatusFrameRate
   */
  fun register(swerveDrive: SwerveDrive) {
    for (wheel in swerveDrive.wheels) {
      register(TalonItem(wheel.azimuthTalon))
      register(TalonItem(wheel.driveTalon))
    }
  }

  /**
   * Configure the Talon with the given ID with the given status frame rates.
   *
   * @param talonId the Talon to registerWith
   * @param rates the status frame rates
   */
  @Deprecated("")
  fun configureStatusFrameRates(talonId: Int, rates: StatusFrameRate?) {
    assert(rates != null)

    if (running) {
      logger.warn("setting status frame rates while telemetry service is running")
    }

    val item = items
      .stream()
      .filter { it -> it is TalonItem && it.deviceId() == talonId }
      .findFirst()

    if (!item.isPresent) {
      throw IllegalArgumentException("talon with id $talonId not found")
    }

    val talonItem = item.get() as TalonItem
    logger.info(
      "setting talon {} ({}) to {}",
      talonItem.description(),
      talonItem.talon.deviceID,
      rates
    )
    rates!!.configure(talonItem.talon)
  }

  /**
   * Get an unmodifiable view of the registered items.
   *
   * @return an unmodifiable Set of Items.
   */
  fun getItems(): Set<Item> {
    return Collections.unmodifiableSet(items)
  }

  /**
   * Unregister an `Item` from a stopped `TelemetryService`.
   *
   * @param item the Item to remove.
   * @throws AssertionError if TelemetryService is running.
   */
  fun remove(item: Item) {
    checkNotStarted()
    if (items.remove(item)) {
      logger.info("removed {}", item)
      return
    }
    throw AssertionError(item.toString())
  }

  private fun checkNotStarted() {
    if (running) {
      throw IllegalStateException("TelemetryService must be stopped.")
    }
  }
}
