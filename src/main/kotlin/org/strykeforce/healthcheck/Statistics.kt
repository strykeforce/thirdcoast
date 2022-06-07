package org.strykeforce.healthcheck

data class DiagnosticLimits(
    val currentMin: Double = 0.0,
    val currentMax: Double = 0.0,
    val speedMin: Double = 0.0,
    val speedMax: Double = 0.0
)

data class DataPoint(
    val voltage: Double,
    val speed: Double,
    val supplyCurrent: Double,
    val statorCurrent: Double
)

internal fun DoubleArray.limitsFor(iteration: Int): DiagnosticLimits {
    val index = iteration * 4
    if (size > index + 3) return DiagnosticLimits(
        this[index], this[index + 1], this[index + 2], this[index + 3]
    )
    return DiagnosticLimits()
}



class DataSeries(val name: String) {

    val data = mutableListOf<DataPoint>()

    fun compute(dataPoint: DataPoint) {
        data += dataPoint
    }

}

class Statistics {
    val dataSeries = mutableListOf<DataSeries>()

    fun newDataSeries(name: String) = DataSeries(name).also { dataSeries += it }
}