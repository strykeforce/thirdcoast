package org.strykeforce.telemetry.measurable

import java.util.function.DoubleSupplier

data class Measure(val name: String, val description: String, val measurement: DoubleSupplier) {
    constructor(name: String, measurement: DoubleSupplier) : this(name, name, measurement)
}
