package org.strykeforce.healthcheck

data class DiagnosticLimits(
    val currentMin: Double = 0.0,
    val currentMax: Double = 0.0,
    val speedMin: Double = 0.0,
    val speedMax: Double = 0.0
)

class DiagnosticData(
    val diagnosticLimits: DiagnosticLimits,
    val voltageMean: Double,
    val speedMean: Double,
    val currentMean: Double
) {

}


internal fun DoubleArray.limitsFor(iteration: Int): DiagnosticLimits {
    val index = iteration * 4
    if (size > index + 3)
        return DiagnosticLimits(this[index], this[index + 1], this[index + 2], this[index + 3])
    return DiagnosticLimits()
}