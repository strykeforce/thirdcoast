package org.strykeforce.thirdcoast.telemetry

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import mu.KotlinLogging
import org.strykeforce.thirdcoast.swerve.SwerveDrive
import org.strykeforce.thirdcoast.telemetry.item.Measurable
import org.strykeforce.thirdcoast.telemetry.item.TalonItem
import java.util.*
import java.util.function.Function

private val logger = KotlinLogging.logger {}

/**
 * The Telemetry service registers [Measurable] instances for data collection and controls the
 * starting and stopping of the service. When active, the services listens for incoming config
 * messages via a HTTP REST service and sends data over UDP.
 */
class TelemetryService(private val telemetryControllerFactory: Function<Inventory, TelemetryController>) {

    // current implementation passes this list to the inventory as a collection via component binding
    // when start is called. The inventory copies this collection into a List, using its index in
    // this list as the inventory id.

    private val items = LinkedHashSet<Measurable>()
    private var telemetryController: TelemetryController? = null

    /** Start the Telemetry service and listen for client connections.  */
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
    fun register(item: Measurable) {
        checkNotStarted()
        if (items.add(item)) {
            logger.info { "registered item ${item.description}" }
            return
        }
        logger.info { "item ${item.description} was already registered" }
    }

    /**
     * Register a collection for telemetry sending.
     *
     * @param collection the collection of Items to register for data collection
     * @throws IllegalStateException if TelemetryService is running.
     */
    fun registerAll(collection: Collection<Measurable>) {
        checkNotStarted()
        items.addAll(collection)
        logger.info { "registered all: $collection" }
    }

    /**
     * Convenience method to register a `TalonSRX` for telemetry sending.
     *
     * @param talon the TalonSRX to register for data collection
     * @throws IllegalStateException if TelemetryService is running.
     */
    fun register(talon: TalonSRX) {
        register(TalonItem(talon))
    }

    /**
     * Convenience method to register a [SwerveDrive] for telemetry sending.
     *
     * @throws IllegalStateException if TelemetryService is running.
     */
    fun register(swerveDrive: SwerveDrive) = swerveDrive.wheels.forEach {
        register(TalonItem(it.azimuthTalon))
        register(TalonItem(it.driveTalon))
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
     * Unregister [item] from a stopped `TelemetryService`.
     *
     * @throws AssertionError if TelemetryService is running.
     */
    fun remove(item: Measurable) {
        checkNotStarted()
        if (items.remove(item)) {
            logger.info { "removed $item" }
            return
        }
        throw AssertionError(item.toString())
    }

    private fun checkNotStarted() {
        if (telemetryController != null) {
            throw IllegalStateException("TelemetryService must be stopped.")
        }
    }
}
