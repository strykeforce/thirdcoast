package org.strykeforce.healthcheck

import edu.wpi.first.util.datalog.DataLog
import edu.wpi.first.util.datalog.DoubleLogEntry
import edu.wpi.first.wpilibj.DataLogManager
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class DiagnosticLimits(
    val currentMin: Double = 0.0, val currentMax: Double = 0.0, val speedMin: Double = 0.0, val speedMax: Double = 0.0
)

data class DataPoint(
    val voltage: Double, val speed: Double, val supplyCurrent: Double, val statorCurrent: Double
)

internal fun DoubleArray.limitsFor(iteration: Int): DiagnosticLimits {
    val index = iteration * 4
    if (size > index + 3) return DiagnosticLimits(
        this[index], this[index + 1], this[index + 2], this[index + 3]
    )
    return DiagnosticLimits()
}


class HealthCheckDataLog(val name: String) {

//    val voltage = DoubleLogEntry(log, "${name}/voltage")
//    val speed = DoubleLogEntry(log, "${name}/speed")
//    val supplyCurrent = DoubleLogEntry(log, "${name}/supplyCurrent")
//    val statorCurrent = DoubleLogEntry(log, "${name}/statorCurrent")
//
    fun append(dataPoint: DataPoint) {
//        voltage.append(dataPoint.voltage)
//        speed.append(dataPoint.speed)
//        supplyCurrent.append(dataPoint.supplyCurrent)
//        statorCurrent.append(dataPoint.statorCurrent)
    }

}

private val kUTC = ZoneId.of("UTC")
private val kDateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").withZone(kUTC)

object Statistics {

    // create log file in default directory with filename that DataLogManager will recognize for automatic cleanup.
    private val dataLogFileName = "FRC_${kDateTimeFormatter.format(LocalDateTime.now(kUTC))}.wpilog".also {
        DataLogManager.start("", it)
    }

    private val dataLogs = mutableListOf<HealthCheckDataLog>()

    fun newDataLog(name: String) = HealthCheckDataLog("/healthcheck/${name}").also { dataLogs += it }
}