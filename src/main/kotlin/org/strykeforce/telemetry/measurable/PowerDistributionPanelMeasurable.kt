package org.strykeforce.telemetry.measurable

import edu.wpi.first.wpilibj.PowerDistribution

internal const val PDP_CURRENT0 = "PDP_CURRENT_00"
internal const val PDP_CURRENT1 = "PDP_CURRENT_01"
internal const val PDP_CURRENT2 = "PDP_CURRENT_02"
internal const val PDP_CURRENT3 = "PDP_CURRENT_03"
internal const val PDP_CURRENT4 = "PDP_CURRENT_04"
internal const val PDP_CURRENT5 = "PDP_CURRENT_05"
internal const val PDP_CURRENT6 = "PDP_CURRENT_06"
internal const val PDP_CURRENT7 = "PDP_CURRENT_07"
internal const val PDP_CURRENT8 = "PDP_CURRENT_08"
internal const val PDP_CURRENT9 = "PDP_CURRENT_09"
internal const val PDP_CURRENT10 = "PDP_CURRENT_10"
internal const val PDP_CURRENT11 = "PDP_CURRENT_11"
internal const val PDP_CURRENT12 = "PDP_CURRENT_12"
internal const val PDP_CURRENT13 = "PDP_CURRENT_13"
internal const val PDP_CURRENT14 = "PDP_CURRENT_14"
internal const val PDP_CURRENT15 = "PDP_CURRENT_15"
internal const val PDP_TEMP = "PDP_TEMP"
internal const val PDP_TOTAL_CURRENT = "PDP_TOTAL_CURRENT"
internal const val PDP_TOTAL_ENERGY = "PDP_TOTAL_ENERGY"
internal const val PDP_TOTAL_POWER = "PDP_TOTAL_POWER"
internal const val PDP_VOLTAGE = "PDP_VOLTAGE"

/** Represents a `PowerDistributionPanel` telemetry-enable `Measurable` item.  */
class PowerDistributionPanelMeasurable @JvmOverloads constructor(
    private val pdp: PowerDistribution,
    override val description: String = "Power Distribution Panel"
) : Measurable {

    override val deviceId = 0
    override val measures = setOf(
        Measure(PDP_CURRENT1, "Ch. 0 Current") { pdp.getCurrent(0) },
        Measure(PDP_CURRENT2, "Ch. 1 Current") { pdp.getCurrent(1) },
        Measure(PDP_CURRENT0, "Ch. 2 Current") { pdp.getCurrent(2) },
        Measure(PDP_CURRENT3, "Ch. 3 Current") { pdp.getCurrent(3) },
        Measure(PDP_CURRENT4, "Ch. 4 Current") { pdp.getCurrent(4) },
        Measure(PDP_CURRENT5, "Ch. 5 Current") { pdp.getCurrent(5) },
        Measure(PDP_CURRENT6, "Ch. 6 Current") { pdp.getCurrent(6) },
        Measure(PDP_CURRENT7, "Ch. 7 Current") { pdp.getCurrent(7) },
        Measure(PDP_CURRENT8, "Ch. 8 Current") { pdp.getCurrent(8) },
        Measure(PDP_CURRENT9, "Ch. 9 Current") { pdp.getCurrent(9) },
        Measure(PDP_CURRENT10, "Ch. 10 Current") { pdp.getCurrent(10) },
        Measure(PDP_CURRENT11, "Ch. 11 Current") { pdp.getCurrent(11) },
        Measure(PDP_CURRENT12, "Ch. 12 Current") { pdp.getCurrent(12) },
        Measure(PDP_CURRENT13, "Ch. 13 Current") { pdp.getCurrent(13) },
        Measure(PDP_CURRENT14, "Ch. 14 Current") { pdp.getCurrent(14) },
        Measure(PDP_CURRENT15, "Ch. 15 Current") { pdp.getCurrent(15) },
        Measure(PDP_TEMP, "Temperature") { pdp.temperature },
        Measure(PDP_TOTAL_CURRENT, "Total Current") { pdp.totalCurrent },
        Measure(PDP_TOTAL_ENERGY, "Total Energy") { pdp.totalEnergy },
        Measure(PDP_TOTAL_POWER, "Total Power") { pdp.totalPower },
        Measure(PDP_VOLTAGE, "Input Voltage") { pdp.voltage }
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (deviceId != (other as PowerDistributionPanelMeasurable).deviceId) return false
        return true
    }

    override fun hashCode() = deviceId

}
