package org.strykeforce.telemetry.measurable

import com.ctre.phoenix6.hardware.TalonFXS
import edu.wpi.first.math.MathUtil
import org.strykeforce.telemetry.talon.*
import org.strykeforce.telemetry.talon.ACCELERATION
import org.strykeforce.telemetry.talon.ACCEL_SCALE
import org.strykeforce.telemetry.talon.BRIDGE_OUTPUT
import org.strykeforce.telemetry.talon.CLOSED_LOOP_D_OUTPUT
import org.strykeforce.telemetry.talon.CLOSED_LOOP_ERROR
import org.strykeforce.telemetry.talon.CLOSED_LOOP_FEED_FWD
import org.strykeforce.telemetry.talon.CLOSED_LOOP_I_OUTPUT
import org.strykeforce.telemetry.talon.CLOSED_LOOP_OUTPUT
import org.strykeforce.telemetry.talon.CLOSED_LOOP_P_OUTPUT
import org.strykeforce.telemetry.talon.CLOSED_LOOP_REFERENCE
import org.strykeforce.telemetry.talon.CLOSED_LOOP_REFERENCE_SLOPE
import org.strykeforce.telemetry.talon.CLOSED_LOOP_SLOT
import org.strykeforce.telemetry.talon.DEVICE_TEMP
import org.strykeforce.telemetry.talon.DIFF_AVG_POS
import org.strykeforce.telemetry.talon.DIFF_AVG_POS_SCALE
import org.strykeforce.telemetry.talon.DIFF_AVG_VEL
import org.strykeforce.telemetry.talon.DIFF_AVG_VEL_SCALE
import org.strykeforce.telemetry.talon.DIFF_CLOSED_LOOP_D_OUTPUT
import org.strykeforce.telemetry.talon.DIFF_CLOSED_LOOP_ERR
import org.strykeforce.telemetry.talon.DIFF_CLOSED_LOOP_FEED_FWD
import org.strykeforce.telemetry.talon.DIFF_CLOSED_LOOP_I_OUT
import org.strykeforce.telemetry.talon.DIFF_CLOSED_LOOP_OUT
import org.strykeforce.telemetry.talon.DIFF_CLOSED_LOOP_P_OUT
import org.strykeforce.telemetry.talon.DIFF_CLOSED_LOOP_REF
import org.strykeforce.telemetry.talon.DIFF_CLOSED_LOOP_REF_SLOPE
import org.strykeforce.telemetry.talon.DIFF_CLOSED_LOOP_SLOT
import org.strykeforce.telemetry.talon.DIFF_DIFF_POS
import org.strykeforce.telemetry.talon.DIFF_DIFF_POS_SCALE
import org.strykeforce.telemetry.talon.DIFF_DIFF_VEL
import org.strykeforce.telemetry.talon.DIFF_DIFF_VEL_SCALE
import org.strykeforce.telemetry.talon.DUTY_CYCLE
import org.strykeforce.telemetry.talon.FWD_LIM
import org.strykeforce.telemetry.talon.HAS_RESET_OCCURRED
import org.strykeforce.telemetry.talon.IS_INVERTED
import org.strykeforce.telemetry.talon.IS_MM_RUNNING
import org.strykeforce.telemetry.talon.IS_PRO_LIC
import org.strykeforce.telemetry.talon.MOTOR_VOLTAGE
import org.strykeforce.telemetry.talon.POSITION
import org.strykeforce.telemetry.talon.POS_SCALE
import org.strykeforce.telemetry.talon.PROCESSOR_TEMP
import org.strykeforce.telemetry.talon.REV_LIM
import org.strykeforce.telemetry.talon.ROTOR_POS
import org.strykeforce.telemetry.talon.ROTOR_POS_SCALE
import org.strykeforce.telemetry.talon.ROTOR_VEL
import org.strykeforce.telemetry.talon.ROTOR_VEL_SCALE
import org.strykeforce.telemetry.talon.STATOR_CURRENT
import org.strykeforce.telemetry.talon.SUPPLY_CURRENT
import org.strykeforce.telemetry.talon.SUPPLY_VOLTAGE
import org.strykeforce.telemetry.talon.TORQUE_CURRENT
import org.strykeforce.telemetry.talon.VELOCITY
import org.strykeforce.telemetry.talon.VEL_SCALE

internal const val ANCILLARY_DEVICE_TEMP = "ANCILLARY_DEVICE_TEMP"
internal const val EXT_MOTOR_TEMP = "EXT_MOTOR_TEMP"
internal const val RAW_PULSE_WIDTH_POS = "Raw Pulse Width Pos"
internal const val RAW_QUADRATURE_POS = "Raw Quadrature Pos"
internal const val RAW_PULSE_WIDTH_VEL = "Raw Pulse Width Vel"
internal const val RAW_QUAD_VEL = "Raw Quadrature Vel"

class TalonFXSMeasureable @JvmOverloads constructor(
    private val talonFXS: TalonFXS,
    override val description: String = "TalonFXS ${talonFXS.deviceID}"
): Measurable {

    val scaleFactor = 1000.0;
    override val deviceId = talonFXS.deviceID
    override val measures = setOf(
        Measure(ACCELERATION, "Acceleration") {talonFXS.acceleration.valueAsDouble},
        Measure(ACCEL_SCALE, "Accel. scaled") {talonFXS.acceleration.valueAsDouble  * scaleFactor},
        Measure(BRIDGE_OUTPUT, "Bridge Output") {talonFXS.bridgeOutput.valueAsDouble},
        Measure(ANCILLARY_DEVICE_TEMP, "Ancillary Device Temp") {talonFXS.ancillaryDeviceTemp.valueAsDouble},
        Measure(DEVICE_TEMP, "Device Temperature C"){talonFXS.deviceTemp.valueAsDouble},
        Measure(EXT_MOTOR_TEMP, "Ext. Motor Temp") {talonFXS.externalMotorTemp.valueAsDouble},
        Measure(DIFF_AVG_POS, "Differential Avg Position") {talonFXS.differentialAveragePosition.valueAsDouble},
        Measure(DIFF_AVG_POS_SCALE,"Diff Avg Pos scaled"){talonFXS.differentialAveragePosition.valueAsDouble * scaleFactor},
        Measure(DIFF_AVG_VEL, "Differential Avg Velocity") {talonFXS.differentialAverageVelocity.valueAsDouble},
        Measure(DIFF_AVG_VEL_SCALE, "Diff Avg Vel scaled"){talonFXS.differentialAverageVelocity.valueAsDouble * scaleFactor},
        Measure(DIFF_DIFF_POS, "Differential Difference Position"){talonFXS.differentialDifferencePosition.valueAsDouble},
        Measure(DIFF_DIFF_POS_SCALE, "Diff Diff Pos scaled"){talonFXS.differentialDifferencePosition.valueAsDouble * scaleFactor},
        Measure(DIFF_DIFF_VEL, "Differential Difference Velocity"){talonFXS.differentialDifferenceVelocity.valueAsDouble},
        Measure(DIFF_DIFF_VEL_SCALE, "Diff Diff Vel scale"){talonFXS.differentialDifferenceVelocity.valueAsDouble * scaleFactor},
        Measure(DUTY_CYCLE, "Applied Duty Cycle"){talonFXS.dutyCycle.value},
        Measure(FWD_LIM, "Forward Limit Switch Closed"){talonFXS.forwardLimit.valueAsDouble},
        Measure(IS_PRO_LIC, "Is Pro Licensed"){talonFXS.isProLicensed.valueAsDouble},
        Measure(IS_MM_RUNNING, "Motion Magic Running"){talonFXS.motionMagicIsRunning.valueAsDouble},
        Measure(MOTOR_VOLTAGE, "Motor Voltage"){talonFXS.motorVoltage.valueAsDouble},
        Measure(POSITION, "Position"){talonFXS.position.valueAsDouble},
        Measure(POS_SCALE, "Pos. scaled"){talonFXS.position.valueAsDouble * scaleFactor},
        Measure(RAW_PULSE_WIDTH_POS, "Raw Pulse Width Pos"){talonFXS.rawPulseWidthPosition.valueAsDouble},
        Measure(RAW_QUADRATURE_POS, "Raw Quad Pos"){talonFXS.rawQuadraturePosition.valueAsDouble},
        Measure(PROCESSOR_TEMP, "Processor Temp"){talonFXS.processorTemp.valueAsDouble},
        Measure(HAS_RESET_OCCURRED, "Has Reset Occurred"){talonFXS.hasResetOccurred().toDouble()},
        Measure(REV_LIM, "Reverse Limit Switch Closed"){talonFXS.reverseLimit.valueAsDouble},
        Measure(ROTOR_POS, "Rotor Position"){talonFXS.rotorPosition.valueAsDouble},
        Measure(ROTOR_POS_SCALE, "Rotor Pos scaled"){ talonFXS.rotorPosition.valueAsDouble * scaleFactor},
        Measure(ROTOR_VEL, "Rotor Velocity"){talonFXS.rotorVelocity.valueAsDouble},
        Measure(ROTOR_VEL_SCALE, "Rotor Vel scaled"){talonFXS.rotorVelocity.valueAsDouble * scaleFactor},
        Measure(STATOR_CURRENT, "Stator Current"){talonFXS.statorCurrent.valueAsDouble},
        Measure(SUPPLY_CURRENT, "Supply Current"){talonFXS.supplyCurrent.valueAsDouble},
        Measure(SUPPLY_VOLTAGE, "Supply Voltage"){talonFXS.supplyVoltage.valueAsDouble},
        Measure(TORQUE_CURRENT, "Torque Current"){talonFXS.torqueCurrent.valueAsDouble},
        Measure(VELOCITY, "Velocity"){talonFXS.velocity.valueAsDouble},
        Measure(VEL_SCALE, "Vel. scaled"){talonFXS.velocity.valueAsDouble * scaleFactor},
        Measure(RAW_PULSE_WIDTH_VEL, "Raw Pulse Width Vel"){talonFXS.rawPulseWidthVelocity.valueAsDouble},
        Measure(RAW_QUAD_VEL, "Raw Quad Vel"){talonFXS.rawQuadratureVelocity.valueAsDouble},

        Measure(CLOSED_LOOP_D_OUTPUT, "Closed Loop Derivative Output"){talonFXS.closedLoopDerivativeOutput.valueAsDouble},
        Measure(CLOSED_LOOP_ERROR, "Closed Loop Error"){talonFXS.closedLoopError.valueAsDouble},
        Measure(CLOSED_LOOP_FEED_FWD, "Closed Loop Feed Forward"){talonFXS.closedLoopFeedForward.valueAsDouble},
        Measure(CLOSED_LOOP_I_OUTPUT, "Closed Loop Integrated Output"){talonFXS.closedLoopIntegratedOutput.valueAsDouble},
        Measure(CLOSED_LOOP_OUTPUT, "Closed Loop Output"){talonFXS.closedLoopError.valueAsDouble},
        Measure(CLOSED_LOOP_P_OUTPUT, "Closed Loop Proportional Output"){talonFXS.closedLoopProportionalOutput.valueAsDouble},
        Measure(CLOSED_LOOP_REFERENCE, "Closed Loop Reference"){talonFXS.closedLoopReference.valueAsDouble},
        Measure(CLOSED_LOOP_REFERENCE_SLOPE, "Closed Loop Reference Slope"){talonFXS.closedLoopReferenceSlope.value},
        Measure(CLOSED_LOOP_SLOT, "Closed Loop Slot"){talonFXS.closedLoopSlot.value.toDouble()},
        Measure(DIFF_CLOSED_LOOP_D_OUTPUT, "Differential Closed Loop D Output"){talonFXS.differentialClosedLoopDerivativeOutput.valueAsDouble},
        Measure(DIFF_CLOSED_LOOP_ERR, "Differential Closed Loop Error"){talonFXS.differentialClosedLoopError.valueAsDouble},
        Measure(DIFF_CLOSED_LOOP_FEED_FWD, "Differential Closed Loop Feed Fwd"){talonFXS.differentialClosedLoopFeedForward.valueAsDouble},
        Measure(DIFF_CLOSED_LOOP_I_OUT, "Differential Closed Loop I Output"){talonFXS.differentialClosedLoopIntegratedOutput.valueAsDouble},
        Measure(DIFF_CLOSED_LOOP_OUT, "Differential Closed Loop Output"){talonFXS.differentialClosedLoopOutput.valueAsDouble},
        Measure(DIFF_CLOSED_LOOP_P_OUT, "Differential Closed Loop P Output"){talonFXS.differentialClosedLoopProportionalOutput.valueAsDouble},
        Measure(DIFF_CLOSED_LOOP_REF, "Differential Closed Loop Reference"){talonFXS.differentialClosedLoopReference.valueAsDouble},
        Measure(DIFF_CLOSED_LOOP_REF_SLOPE, "Differential Closed Loop Reference Slope"){talonFXS.differentialClosedLoopReferenceSlope.valueAsDouble},
        Measure(DIFF_CLOSED_LOOP_SLOT, "Differential Closed Loop Slot"){talonFXS.differentialClosedLoopSlot.valueAsDouble},
        Measure(PULSE_WIDTH_POSITION, "Pulse Width Position") {((MathUtil.inputModulus(talonFXS.rawPulseWidthPosition.valueAsDouble,0.0, 1.0))}
    )

    override fun equals(other: Any?): Boolean {
        if(this === other) return  true
        if(javaClass != other?.javaClass) return false

        other as TalonFXSMeasureable
        if(deviceId != other.deviceId) return  false
        return true
    }

    override fun hashCode() = deviceId

}