package org.strykeforce.telemetry.talon

import com.ctre.phoenix6.hardware.TalonFX
import org.strykeforce.telemetry.measurable.Measurable
import org.strykeforce.telemetry.measurable.Measure
import org.strykeforce.telemetry.measurable.toDouble
import kotlin.concurrent.timerTask

//Device Status
internal const val ACCELERATION = "ACCELERATION"
internal const val ACCEL_SCALE = "ACCEL_SCALE"
internal const val BRIDGE_OUTPUT = "BRIDGE_OUTPUT"
internal const val DEVICE_TEMP = "DEVICE_TEMP"
internal const val DIFF_AVG_POS = "DIFF_AVG_POS"
internal const val DIFF_AVG_POS_SCALE = "DIFF_AVG_POS_SCALE"
internal const val DIFF_AVG_VEL = "DIFF_AVG_VEL"
internal const val DIFF_AVG_VEL_SCALE = "DIFF_AVG_VEL_SCALE"
internal const val DIFF_DIFF_POS = "DIFF_DIFF_POS"
internal const val DIFF_DIFF_POS_SCALE = "DIFF_DIFF_POS_SCALE"
internal const val DIFF_DIFF_VEL = "DIFF_DIFF_VEL"
internal const val DIFF_DIFF_VEL_SCALE = "DIFF_DIFF_VEL_SCALE"
internal const val DUTY_CYCLE = "DUTY_CYCLE"
internal const val FWD_LIM = "FWD_LIM"
internal const val IS_PRO_LIC = "IS_PRO_LIC"
internal const val IS_MM_RUNNING = "IS_MM_RUNNING"
internal const val MOTOR_VOLTAGE = "MOTOR_VOLTAGE"
internal const val POSITION = "POSITION"
internal const val POS_SCALE = "POS_SCALE"
internal const val PROCESSOR_TEMP = "PROCESSOR_TEMP"
internal const val HAS_RESET_OCCURRED = "HAS_RESET_OCCURRED"
internal const val REV_LIM = "REV_LIM"
internal const val ROTOR_POS = "ROTOR_POS"
internal const val ROTOR_POS_SCALE = "ROTOR_POS_SCALE"
internal const val ROTOR_VEL = "ROTOR_VEL"
internal const val ROTOR_VEL_SCALE = "ROTOR_VEL_SCALE"
internal const val STATOR_CURRENT = "STATOR_CURRENT"
internal const val SUPPLY_CURRENT = "SUPPLY_CURRENT"
internal const val SUPPLY_VOLTAGE = "SUPPLY_VOLTAGE"
internal const val TORQUE_CURRENT = "TORQUE_CURRENT"
internal const val VELOCITY = "VELOCITY"
internal const val VEL_SCALE = "VEL_SCALE"

//PID
internal const val CLOSED_LOOP_D_OUTPUT = "CLOSED_LOOP_D_OUTPUT"
internal const val CLOSED_LOOP_ERROR = "CLOSED_LOOP_ERROR"
internal const val CLOSED_LOOP_FEED_FWD = "CLOSED_LOOP_FEED_FWD"
internal const val CLOSED_LOOP_I_OUTPUT = "CLOSED_LOOP_I_OUTPUT"
internal const val CLOSED_LOOP_OUTPUT = "CLOSED_LOOP_OUTPUT"
internal const val CLOSED_LOOP_P_OUTPUT = "CLOSED_LOOP_P_OUTPUT"
internal const val CLOSED_LOOP_REFERENCE = "CLOSED_LOOP_REFERENCE"
internal const val CLOSED_LOOP_REFERENCE_SLOPE = "CLOSED_LOOP_REFERENCE_SLOPE"
internal const val CLOSED_LOOP_SLOT = "CLOSED_LOOP_SLOT"
internal const val DIFF_CLOSED_LOOP_D_OUTPUT = "DIFF_CLOSED_LOOP_D_OUTPUT"
internal const val DIFF_CLOSED_LOOP_ERR = "DIFF_CLOSED_LOOP_ERR"
internal const val DIFF_CLOSED_LOOP_FEED_FWD = "DIFF_CLOSED_LOOP_FEED_FWD"
internal const val DIFF_CLOSED_LOOP_I_OUT = "DIFF_CLOSED_LOOP_I_OUT"
internal const val DIFF_CLOSED_LOOP_OUT = "DIFF_CLOSED_LOOP_OUT"
internal const val DIFF_CLOSED_LOOP_P_OUT = "DIFF_CLOSED_LOOP_P_OUT"
internal const val DIFF_CLOSED_LOOP_REF = "DIFF_CLOSED_LOOP_REF"
internal const val DIFF_CLOSED_LOOP_REF_SLOPE = "DIFF_CLOSED_LOOP_REF_SLOPE"
internal const val DIFF_CLOSED_LOOP_SLOT = "DIFF_CLOSED_LOOP_SLOT"


internal const val APPLIED_ROTOR_POLARITY = "APPLIED_ROTOR_POLARITY"
internal const val BRIDGE_OUTPUT_COAST = "BRIDGE_OUTPUT_COAST"
internal const val BRIDGE_OUTPUT_BRAKE = "BRIDGE_OUTPUT_BRAKE"
internal const val BRIDGE_OUTPUT_TRAPZ = "BRIDGE_OUTPUT_TRAPZ"
internal const val BRIDGE_OUTPUT_FOC_TORQE = "BRIDGE_OUTPUT_FOC_TORQUE"
internal const val BRIDGE_OUTPUT_MUSIC = "BRIDGE_OUTPUT_MUSIC"
internal const val BRIDGE_OUTPUT_FOC_EASY = "BRIDGE_OUTPUT_FOC_EASY"
internal const val BRIDGE_OUTPUT_FAULT_BRAKE = "BRIDGE_OUTPUT_FAULT_BRAKE"
internal const val BRIDGE_OUTPUT_FAULT_COAST = "BRIDGE_OUTPUT_FAULT_COAST"
internal const val CONTROL_MODE = "CONTROL_MODE"
internal const val DEVICE_ENABLE = "DEVICE_ENABLE"


internal const val SIM_STATE = "SIM_STATE"
internal const val VERSION = "VERSION"
internal const val VERSION_BUG = "VERSION_BUG"
internal const val VERSION_BUILD = "VERSION_BUILD"
internal const val VERSION_MAJOR = "VERSION_MAJOR"
internal const val VERSION_MINOR = "VERSION_MINOR"
internal const val APPLIED_CONTROL = "APPLIED_CONTROL"
internal const val IS_INVERTED = "IS_INVERTED"
internal const val IS_ALIVE = "IS_ALIVE"
internal const val IS_SAFETY_ENABLED = "IS_SAFETY_ENABLED"
internal const val CLOSED_LOOP_TARGET = "CLOSED_LOOP_TARGET"



class TalonFXMeasureable @JvmOverloads constructor(
    private val talonFX: TalonFX,
    override val description: String = "TalonFX ${talonFX.deviceID}"
): Measurable {

    val scaleFactor = 1000.0;
    override val deviceId = talonFX.deviceID
    override val measures = setOf(
        Measure(ACCELERATION, "Acceleration") {talonFX.acceleration.valueAsDouble},
        Measure(ACCEL_SCALE, "Accel. scaled") {talonFX.acceleration.valueAsDouble  * scaleFactor},
        Measure(BRIDGE_OUTPUT, "Bridge Output") {talonFX.bridgeOutput.valueAsDouble},
        Measure(DEVICE_TEMP, "Device Temperature C"){talonFX.deviceTemp.valueAsDouble},
        Measure(DIFF_AVG_POS, "Differential Avg Position") {talonFX.differentialAveragePosition.valueAsDouble},
        Measure(DIFF_AVG_POS_SCALE,"Diff Avg Pos scaled"){talonFX.differentialAveragePosition.valueAsDouble * scaleFactor},
        Measure(DIFF_AVG_VEL, "Differential Avg Velocity") {talonFX.differentialAverageVelocity.valueAsDouble},
        Measure(DIFF_AVG_VEL_SCALE, "Diff Avg Vel scaled"){talonFX.differentialAverageVelocity.valueAsDouble * scaleFactor},
        Measure(DIFF_DIFF_POS, "Differential Difference Position"){talonFX.differentialDifferencePosition.valueAsDouble},
        Measure(DIFF_DIFF_POS_SCALE, "Diff Diff Pos scaled"){talonFX.differentialDifferencePosition.valueAsDouble * scaleFactor},
        Measure(DIFF_DIFF_VEL, "Differential Difference Velocity"){talonFX.differentialDifferenceVelocity.valueAsDouble},
        Measure(DIFF_DIFF_VEL_SCALE, "Diff Diff Vel scale"){talonFX.differentialDifferenceVelocity.valueAsDouble * scaleFactor},
        Measure(DUTY_CYCLE, "Applied Duty Cycle"){talonFX.dutyCycle.value},
        Measure(FWD_LIM, "Forward Limit Switch Closed"){talonFX.forwardLimit.valueAsDouble},
        Measure(IS_PRO_LIC, "Is Pro Licensed"){talonFX.isProLicensed.valueAsDouble},
        Measure(IS_MM_RUNNING, "Motion Magic Running"){talonFX.motionMagicIsRunning.valueAsDouble},
        Measure(MOTOR_VOLTAGE, "Motor Voltage"){talonFX.motorVoltage.valueAsDouble},
        Measure(POSITION, "Position"){talonFX.position.valueAsDouble},
        Measure(POS_SCALE, "Pos. scaled"){talonFX.position.valueAsDouble * scaleFactor},
        Measure(PROCESSOR_TEMP, "Processor Temp"){talonFX.processorTemp.valueAsDouble},
        Measure(HAS_RESET_OCCURRED, "Has Reset Occurred"){talonFX.hasResetOccurred().toDouble()},
        Measure(REV_LIM, "Reverse Limit Switch Closed"){talonFX.reverseLimit.valueAsDouble},
        Measure(ROTOR_POS, "Rotor Position"){talonFX.rotorPosition.valueAsDouble},
        Measure(ROTOR_POS_SCALE, "Rotor Pos scaled"){ talonFX.rotorPosition.valueAsDouble * scaleFactor},
        Measure(ROTOR_VEL, "Rotor Velocity"){talonFX.rotorVelocity.valueAsDouble},
        Measure(ROTOR_VEL_SCALE, "Rotor Vel scaled"){talonFX.rotorVelocity.valueAsDouble * scaleFactor},
        Measure(STATOR_CURRENT, "Stator Current"){talonFX.statorCurrent.valueAsDouble},
        Measure(SUPPLY_CURRENT, "Supply Current"){talonFX.supplyCurrent.valueAsDouble},
        Measure(SUPPLY_VOLTAGE, "Supply Voltage"){talonFX.supplyVoltage.valueAsDouble},
        Measure(TORQUE_CURRENT, "Torque Current"){talonFX.torqueCurrent.valueAsDouble},
        Measure(VELOCITY, "Velocity"){talonFX.velocity.valueAsDouble},
        Measure(VEL_SCALE, "Vel. scaled"){talonFX.velocity.valueAsDouble * scaleFactor},
        Measure(IS_INVERTED, "Is Inverted"){talonFX.inverted.toDouble()},

        Measure(CLOSED_LOOP_D_OUTPUT, "Closed Loop Derivative Output"){talonFX.closedLoopDerivativeOutput.valueAsDouble},
        Measure(CLOSED_LOOP_ERROR, "Closed Loop Error"){talonFX.closedLoopError.valueAsDouble},
        Measure(CLOSED_LOOP_FEED_FWD, "Closed Loop Feed Forward"){talonFX.closedLoopFeedForward.valueAsDouble},
        Measure(CLOSED_LOOP_I_OUTPUT, "Closed Loop Integrated Output"){talonFX.closedLoopIntegratedOutput.valueAsDouble},
        Measure(CLOSED_LOOP_OUTPUT, "Closed Loop Output"){talonFX.closedLoopError.valueAsDouble},
        Measure(CLOSED_LOOP_P_OUTPUT, "Closed Loop Proportional Output"){talonFX.closedLoopProportionalOutput.valueAsDouble},
        Measure(CLOSED_LOOP_REFERENCE, "Closed Loop Reference"){talonFX.closedLoopReference.valueAsDouble},
        Measure(CLOSED_LOOP_REFERENCE_SLOPE, "Closed Loop Reference Slope"){talonFX.closedLoopReferenceSlope.value},
        Measure(CLOSED_LOOP_SLOT, "Closed Loop Slot"){talonFX.closedLoopSlot.value.toDouble()},
        Measure(DIFF_CLOSED_LOOP_D_OUTPUT, "Differential Closed Loop D Output"){talonFX.differentialClosedLoopDerivativeOutput.valueAsDouble},
        Measure(DIFF_CLOSED_LOOP_ERR, "Differential Closed Loop Error"){talonFX.differentialClosedLoopError.valueAsDouble},
        Measure(DIFF_CLOSED_LOOP_FEED_FWD, "Differential Closed Loop Feed Fwd"){talonFX.differentialClosedLoopFeedForward.valueAsDouble},
        Measure(DIFF_CLOSED_LOOP_I_OUT, "Differential Closed Loop I Output"){talonFX.differentialClosedLoopIntegratedOutput.valueAsDouble},
        Measure(DIFF_CLOSED_LOOP_OUT, "Differential Closed Loop Output"){talonFX.differentialClosedLoopOutput.valueAsDouble},
        Measure(DIFF_CLOSED_LOOP_P_OUT, "Differential Closed Loop P Output"){talonFX.differentialClosedLoopProportionalOutput.valueAsDouble},
        Measure(DIFF_CLOSED_LOOP_REF, "Differential Closed Loop Reference"){talonFX.differentialClosedLoopReference.valueAsDouble},
        Measure(DIFF_CLOSED_LOOP_REF_SLOPE, "Differential Closed Loop Reference Slope"){talonFX.differentialClosedLoopReferenceSlope.valueAsDouble},
        Measure(DIFF_CLOSED_LOOP_SLOT, "Differential Closed Loop Slot"){talonFX.differentialClosedLoopSlot.valueAsDouble},
    )

    override fun equals(other: Any?) : Boolean {
        if(this === other) return  true
        if(javaClass != other?.javaClass) return  false

        other as TalonFXMeasureable
        if(deviceId != other.deviceId) return false
        return true
    }

    override fun hashCode() = deviceId
}