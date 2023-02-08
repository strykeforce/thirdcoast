package org.strykeforce.healthcheck.internal

import edu.wpi.first.wpilibj.DataLogManager
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class DiagnosticLimits(
    val currentMin: Double = 0.0, val currentMax: Double = 0.0, val speedMin: Double = 0.0, val speedMax: Double = 0.0
)

class HealthCheckData() {
    val voltage: MutableList<Double> = mutableListOf()
    val speed: MutableList<Double> = mutableListOf()
    val supplyCurrent: MutableList<Double> = mutableListOf()
    val statorCurrent: MutableList<Double> = mutableListOf()

    val averageVoltage
        get() = voltage.average()

    val averageSpeed
        get() = speed.average()

    val averageSupplyCurrent
        get() = supplyCurrent.average()

    val averageStatorCurrent
        get() = statorCurrent.average()

}

internal fun DoubleArray.limitsFor(iteration: Int): DiagnosticLimits {
    val index = iteration * 4
    if (size > index + 3) return DiagnosticLimits(
        this[index], this[index + 1], this[index + 2], this[index + 3]
    )
    return DiagnosticLimits()
}