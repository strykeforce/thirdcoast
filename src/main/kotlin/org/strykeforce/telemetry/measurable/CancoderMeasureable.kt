package org.strykeforce.telemetry.measurable

import com.ctre.phoenix6.hardware.CANcoder
import org.strykeforce.telemetry.talon.HAS_RESET_OCCURRED
import org.strykeforce.telemetry.talon.IS_PRO_LIC
import org.strykeforce.telemetry.talon.POS_SCALE

internal  const val MAGNET_HEALTH = "MAGNET_HEALTH"
//internal const val POSITION = "POSITION"
internal const val POSITION_SCALED = "POS_SCALED"
internal const val POSITION_SINCE_BOOT = "POSITION_SINCE_BOOT"
internal const val POS_BOOT_SCALED = "POS_BOOT_SCALED"
internal const val ABS_POSITION = "ABS_POSITION"
internal const val ABS_POS_SCALED = "ABS_POS_SCALED"
internal const val SUPPLY_VOLTAGE = "SUPPLY_VOLTAGE"
internal const val UNFILTERED_VELOCITY = "UNFILTERED_VELOCITY"
internal const val VELOCITY = "VELOCITY"


class CancoderMeasureable @JvmOverloads constructor(
    private val cancoder: CANcoder,
    override val description: String = "Cancoder ${cancoder.deviceID}"
): Measurable {

    private val scaleFactor = 1000.0;
    override val deviceId = cancoder.deviceID
    override val measures = setOf(
        Measure(MAGNET_HEALTH, "Magnet Health") {cancoder.magnetHealth.valueAsDouble},
        Measure(POSITION, "Position") {cancoder.position.valueAsDouble},
        Measure(POS_SCALE, "Pos. Scaled") {cancoder.position.valueAsDouble * scaleFactor},
        Measure(POSITION_SINCE_BOOT, "Position Since Boot") {cancoder.positionSinceBoot.valueAsDouble},
        Measure(POS_BOOT_SCALED, "Pos. since Boot Scaled") {cancoder.positionSinceBoot.valueAsDouble * scaleFactor},
        Measure(ABS_POSITION, "Absolute Position") {cancoder.absolutePosition.valueAsDouble},
        Measure(ABS_POS_SCALED, "Absolute Pos. Scaled") {cancoder.absolutePosition.valueAsDouble * scaleFactor},
        Measure(SUPPLY_VOLTAGE, "Supply Voltage") {cancoder.supplyVoltage.valueAsDouble},
        Measure(UNFILTERED_VELOCITY, "Unfiltered Velocity") {cancoder.unfilteredVelocity.valueAsDouble},
        Measure(VELOCITY, "Velocity") {cancoder.velocity.valueAsDouble},
        Measure(IS_PRO_LIC, "Is Pro Licensed") {cancoder.isProLicensed.valueAsDouble},
        Measure(HAS_RESET_OCCURRED, "Has Reset Occurred") {cancoder.resetOccurredChecker.asBoolean.toDouble()}
    )
}