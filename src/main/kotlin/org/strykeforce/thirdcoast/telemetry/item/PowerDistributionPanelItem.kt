package org.strykeforce.thirdcoast.telemetry.item

import edu.wpi.first.wpilibj.PowerDistributionPanel
import java.util.function.DoubleSupplier

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
class PowerDistributionPanelItem @JvmOverloads constructor(
  private val pdp: PowerDistributionPanel,
  override val description: String = "Power Distribution Panel"
) : Measurable {

  override val deviceId = 0
  override val type = "pdp"
  override val measures = setOf(
    Measure(PDP_CURRENT1, "Ch. 0 Current"),
    Measure(PDP_CURRENT2, "Ch. 1 Current"),
    Measure(PDP_CURRENT0, "Ch. 2 Current"),
    Measure(PDP_CURRENT3, "Ch. 3 Current"),
    Measure(PDP_CURRENT4, "Ch. 4 Current"),
    Measure(PDP_CURRENT5, "Ch. 5 Current"),
    Measure(PDP_CURRENT6, "Ch. 6 Current"),
    Measure(PDP_CURRENT7, "Ch. 7 Current"),
    Measure(PDP_CURRENT8, "Ch. 8 Current"),
    Measure(PDP_CURRENT9, "Ch. 9 Current"),
    Measure(PDP_CURRENT10, "Ch. 10 Current"),
    Measure(PDP_CURRENT11, "Ch. 11 Current"),
    Measure(PDP_CURRENT12, "Ch. 12 Current"),
    Measure(PDP_CURRENT13, "Ch. 13 Current"),
    Measure(PDP_CURRENT14, "Ch. 14 Current"),
    Measure(PDP_CURRENT15, "Ch. 15 Current"),
    Measure(PDP_TEMP, "Temperature"),
    Measure(PDP_TOTAL_CURRENT, "Total Current"),
    Measure(PDP_TOTAL_ENERGY, "Total Energy"),
    Measure(PDP_TOTAL_POWER, "Total Power"),
    Measure(PDP_VOLTAGE, "Input Voltage")
  )

  override fun measurementFor(measure: Measure): DoubleSupplier {

    return when (measure.name) {
      PDP_CURRENT0 -> DoubleSupplier { pdp.getCurrent(0) }
      PDP_CURRENT1 -> DoubleSupplier { pdp.getCurrent(1) }
      PDP_CURRENT2 -> DoubleSupplier { pdp.getCurrent(2) }
      PDP_CURRENT3 -> DoubleSupplier { pdp.getCurrent(3) }
      PDP_CURRENT4 -> DoubleSupplier { pdp.getCurrent(4) }
      PDP_CURRENT5 -> DoubleSupplier { pdp.getCurrent(5) }
      PDP_CURRENT6 -> DoubleSupplier { pdp.getCurrent(6) }
      PDP_CURRENT7 -> DoubleSupplier { pdp.getCurrent(7) }
      PDP_CURRENT8 -> DoubleSupplier { pdp.getCurrent(8) }
      PDP_CURRENT9 -> DoubleSupplier { pdp.getCurrent(9) }
      PDP_CURRENT10 -> DoubleSupplier { pdp.getCurrent(10) }
      PDP_CURRENT11 -> DoubleSupplier { pdp.getCurrent(11) }
      PDP_CURRENT12 -> DoubleSupplier { pdp.getCurrent(12) }
      PDP_CURRENT13 -> DoubleSupplier { pdp.getCurrent(13) }
      PDP_CURRENT14 -> DoubleSupplier { pdp.getCurrent(14) }
      PDP_CURRENT15 -> DoubleSupplier { pdp.getCurrent(15) }
      PDP_TEMP -> DoubleSupplier { pdp.temperature }
      PDP_TOTAL_CURRENT -> DoubleSupplier { pdp.totalCurrent }
      PDP_TOTAL_ENERGY -> DoubleSupplier { pdp.totalEnergy }
      PDP_TOTAL_POWER -> DoubleSupplier { pdp.totalPower }
      PDP_VOLTAGE -> DoubleSupplier { pdp.voltage }
      else -> DoubleSupplier { 2767.0 }
    }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    if (deviceId != (other as PowerDistributionPanelItem).deviceId) return false
    return true
  }

  override fun hashCode() = deviceId

}
