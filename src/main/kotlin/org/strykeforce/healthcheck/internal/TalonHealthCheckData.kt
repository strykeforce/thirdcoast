package org.strykeforce.healthcheck.internal

import com.ctre.phoenix.motorcontrol.can.BaseTalon

data class DiagnosticLimits(
    val currentMin: Double = 0.0, val currentMax: Double = 0.0, val speedMin: Double = 0.0, val speedMax: Double = 0.0
)

class TalonHealthCheckData(private val talon:BaseTalon) {
    val voltage: MutableList<Double> = mutableListOf()
    val speed: MutableList<Double> = mutableListOf()
    val supplyCurrent: MutableList<Double> = mutableListOf()
    val statorCurrent: MutableList<Double> = mutableListOf()

    fun measure() {
        voltage.add(talon.motorOutputVoltage)
        speed.add(talon.selectedSensorVelocity)
        supplyCurrent.add(talon.supplyCurrent)
        statorCurrent.add(talon.statorCurrent)
    }

    val id
        get() = talon.deviceID

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