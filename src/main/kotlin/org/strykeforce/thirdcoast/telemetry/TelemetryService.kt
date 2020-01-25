package org.strykeforce.thirdcoast.telemetry

import com.ctre.phoenix.motorcontrol.can.TalonFX
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import mu.KotlinLogging
import org.strykeforce.thirdcoast.swerve.SwerveDrive
import org.strykeforce.thirdcoast.talon.TalonFXItem
import org.strykeforce.thirdcoast.telemetry.item.Measurable
import org.strykeforce.thirdcoast.telemetry.item.TalonSRXItem
import java.util.*
import java.util.function.Function

private val logger = KotlinLogging.logger {}

/**
 * The Telemetry service is the main interface for client applications that are telemetry-enabled. It registers
 * [Measurable] instances for data collection and controls the starting and stopping of the service. When active,
 * the services listens for incoming control messages via a HTTP REST-like service and sends data over UDP.
 *
 * The constructor takes a factory function to create a [TelemetryController] instance with a given [Inventory].
 * The default [TelemetryController] has a constructor that serves this purpose, for example:
 * ```
 * TelemetryService ts = new TelemetryService(TelemetryController::new);
 * ts.register(talon);
 * ts.start();
 * ```
 */
class TelemetryService(private val telemetryControllerFactory: Function<Inventory, TelemetryController>) {

  // Current implementation passes the `items` list to the inventory as a collection when start is called. The inventory
  // sorts and copies this collection into a List, using its index in this list as the inventory id. This should provide
  // a stable order of measurable items that assists the Grapher client when saving its configuration.

  private val items = LinkedHashSet<Measurable>()
  private var telemetryController: TelemetryController? = null

  /**
   * Start the Telemetry service and listen for client connections.  A new instance of [TelemetryController] is created
   * that reflects the current list of [Measurable] items.
   */
  fun start() {
    if (telemetryController != null) {
      logger.info("already started")
      return
    }
    telemetryController = telemetryControllerFactory.apply(RobotInventory(items)).also { it.start() }
    logger.info("started telemetry controller")
  }

  /** Stop the Telemetry service.  */
  fun stop() {
    if (telemetryController == null) {
      logger.info("already stopped")
      return
    }
    telemetryController?.shutdown()
    telemetryController = null
    logger.info("stopped")
  }

  /**
   * Un-register all [Measurable] items.
   *
   * @throws IllegalStateException if TelemetryService is running.
   */
  fun clear() {
    check(telemetryController == null) { "TelemetryService must be stopped to clear registered items." }
    items.clear()
    logger.info("item set was cleared")
  }

  /**
   * Registers an Item for telemetry sending.
   *
   * @param item the [Measurable] to register for data collection
   * @throws IllegalStateException if TelemetryService is running.
   */
  fun register(item: Measurable) {
    check(telemetryController == null) { "TelemetryService must be stopped to register an item." }
    if (items.add(item)) {
      logger.info { "registered item ${item.description}" }
      return
    }
    logger.info { "item ${item.description} was already registered" }
  }

  /**
   * Register a collection of [Measurable] items for telemetry sending.
   *
   * @param collection the collection of Items to register for data collection
   * @throws IllegalStateException if TelemetryService is running.
   */
  fun registerAll(collection: Collection<Measurable>) = collection.forEach(this::register)

  /**
   * Convenience method to register a `TalonSRX` for telemetry sending.
   *
   * @param talon the TalonSRX to register for data collection
   * @throws IllegalStateException if TelemetryService is running.
   */
  fun register(talon: TalonSRX) {
    register(TalonSRXItem(talon))
  }

  /**
   * Convenience method to register a `TalonFX` for telemetry sending.
   *
   * @param talon the TalonSRX to register for data collection
   * @throws IllegalStateException if TelemetryService is running.
   */
  fun register(talon: TalonFX) {
    register(TalonFXItem(talon))
  }

  /**
   * Convenience method to register a [SwerveDrive] for telemetry sending.
   *
   * @throws IllegalStateException if TelemetryService is running.
   */
  fun register(swerveDrive: SwerveDrive) = swerveDrive.wheels.forEach {
    register(TalonSRXItem(it.azimuthTalon))
    if(it.driveTalon is TalonSRX) register(TalonSRXItem(it.driveTalon as TalonSRX))
    else if(it.driveTalon is TalonFX) register(TalonFXItem(it.driveTalon as TalonFX))
    else throw IllegalArgumentException()
  }

  /**
   * Get an unmodifiable view of the registered items.
   *
   * @return an unmodifiable Set of Items.
   */
  fun getItems(): Set<Measurable> {
    return Collections.unmodifiableSet(items)
  }

  /**
   * Unregister a [Measurable] item. This service must be stopped first.
   *
   * @throws AssertionError if TelemetryService is running.
   */
  fun remove(item: Measurable) {
    check(telemetryController == null) { "TelemetryService must be stopped to remove an item." }
    if (items.remove(item)) {
      logger.info { "removed $item" }
      return
    }
    throw AssertionError(item.toString())
  }

}
