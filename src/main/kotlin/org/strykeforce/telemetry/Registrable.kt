package org.strykeforce.telemetry

/**
 * A class that has one or more [Measurable] objects that should be registered with the [TelemetryService].
 *
 */
interface Registrable {

    /**
     * Register this class with a [TelemetryService].
     *
     * This method should be called once during robot initialization before the [TelemetryService] is started.
     *
     * @param telemetryService the initialized TelemetryService to register with
     * @throws IllegalStateException if TelemetryService is currently running
     */
    fun registerWith(telemetryService: TelemetryService)
}