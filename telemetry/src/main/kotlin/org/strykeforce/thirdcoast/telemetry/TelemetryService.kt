package org.strykeforce.thirdcoast.telemetry

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import mu.KotlinLogging
import org.strykeforce.thirdcoast.swerve.SwerveDrive
import org.strykeforce.thirdcoast.telemetry.graphable.Graphable
import org.strykeforce.thirdcoast.telemetry.graphable.TalonGraphable
import java.util.*
import java.util.function.Function

private val logger = KotlinLogging.logger {}

/**
 * The Telemetry service registers [Graphable] instances for data collection and controls the
 * starting and stopping of the service. When active, the services listens for incoming config
 * messages via a HTTP REST service and sends data over UDP.
 */
class TelemetryService(private val telemetryControllerFactory: Function<Inventory, TelemetryController>) {

    // current implementation passes this list to the inventory as a collection via component binding
    // when start is called. The inventory copies this collection into a List, using its index in
    // this list as the inventory id.

    private val graphables = LinkedHashSet<Graphable>()
    private var telemetryController: TelemetryController? = null

    /** Start the Telemetry service and listen for client connections.  */
    fun start() {
        if (telemetryController != null) {
            logger.info("already started")
            return
        }
        telemetryController = telemetryControllerFactory.apply(RobotInventory(graphables)).also { it.start() }
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
        graphables.clear()
        logger.info("graphable set was cleared")
    }

    /**
     * Registers an Graphable for telemetry sending.
     *
     * @param graphable the Graphable to register for data collection
     * @throws IllegalStateException if TelemetryService is running.
     */
    fun register(graphable: Graphable) {
        checkNotStarted()
        if (graphables.add(graphable)) {
            logger.info { "registered graphable ${graphable.description}" }
            return
        }
        logger.info { "graphable ${graphable.description} was already registered" }
    }

    /**
     * Register a collection for telemetry sending.
     *
     * @param collection the collection of Items to register for data collection
     * @throws IllegalStateException if TelemetryService is running.
     */
    fun registerAll(collection: Collection<Graphable>) {
        checkNotStarted()
        graphables.addAll(collection)
        logger.info { "registered all: $collection" }
    }

    /**
     * Convenience method to register a `TalonSRX` for telemetry sending.
     *
     * @param talon the TalonSRX to register for data collection
     * @throws IllegalStateException if TelemetryService is running.
     */
    fun register(talon: TalonSRX) {
        register(TalonGraphable(talon))
    }

    /**
     * Convenience method to register a [SwerveDrive] for telemetry sending.
     *
     * @throws IllegalStateException if TelemetryService is running.
     */
    fun register(swerveDrive: SwerveDrive) = swerveDrive.wheels.forEach {
        register(TalonGraphable(it.azimuthTalon))
        register(TalonGraphable(it.driveTalon))
    }

    /**
     * Get an unmodifiable view of the registered graphables.
     *
     * @return an unmodifiable Set of Items.
     */
    fun getGraphables(): Set<Graphable> {
        return Collections.unmodifiableSet(graphables)
    }

    /**
     * Unregister [graphable] from a stopped `TelemetryService`.
     *
     * @throws AssertionError if TelemetryService is running.
     */
    fun remove(graphable: Graphable) {
        checkNotStarted()
        if (graphables.remove(graphable)) {
            logger.info { "removed $graphable" }
            return
        }
        throw AssertionError(graphable.toString())
    }

    private fun checkNotStarted() {
        if (telemetryController != null) {
            throw IllegalStateException("TelemetryService must be stopped.")
        }
    }
}
