package org.strykeforce.telemetry.measurable

import com.ctre.phoenix6.hardware.TalonFX
import jdk.jfr.Description

internal const val APPLIED_ROTOR_POLARITY = "APPLIED_ROTOR_POLARITY"
internal const val BRIDGE_OUTPUT_COAST = "BRIDGE_OUTPUT_COAST"
internal const val BRIDGE_OUTPUT_BRAKE = "BRIDGE_OUTPUT_BRAKE"
internal const val BRIDGE_OUTPUT_TRAPZ = "BRIDGE_OUTPUT_TRAPZ"
internal const val BRIDGE_OUTPUT_FOC_TORQE = "BRIDGE_OUTPUT_FOC_TORQUE"
internal const val BRIDGE_OUTPUT_MUSIC = "BRIDGE_OUTPUT_MUSIC"
internal const val BRIDGE_OUTPUT_FOC_EASY = "BRIDGE_OUTPUT_FOC_EASY"
internal const val BRIDGE_OUTPUT_FAULT_BRAKE = "BRIDGE_OUTPUT_FAULT_BRAKE"
internal const val BRIDGE_OUTPUT_FAULT_COAST = "BRIDGE_OUTPUT_FAULT_COAST"
internal const val CLOSED_LOOP_DERIVATIVE_OUTPUT = "CLOSED_LOOP_DERIVATIVE_OUTPUT"
internal const val CLOSED_LOOP_FEED_FWD = "CLOSED_LOOP_FEED_FWD"
internal const val CLOSED_LOOP_INTEGRATED_OUTPUT = "CLOSED_LOOP_INTEGRATED_OUTPUT"
internal const val CLOSED_LOOP_OUTPUT = "CLOSED_LOOP_OUTPUT"
internal const val CLOSED_LOOP_PROPORTIONAL_OUTPUT = "CLOSED_LOOP_PROPORTIONAL_OUTPUT"
internal const val CLOSED_LOOP_REFERENCE = "CLOSED_LOOP_REFERENCE"
internal const val CLOSED_LOOP_REFERENCE_SLOPE = "CLOSED_LOOP_REFERENCE_SLOPE"
internal const val CLOSED_LOOP_SLOT = "CLOSED_LOOP_SLOT"
internal const val CONTROL_MODE = "CONTROL_MODE"
internal const val DEVICE_ENABLE = "DEVICE_ENABLE"
internal const val DEVICE_TEMP = "DEVICE_TEMP"
internal const val DUTY_CYCLE = "DUTY_CYCLE"
internal const val FAULT_BOOT_DURING_EN = "FAULT_BOOT_DURING_EN"
internal const val FAULT_DEVICE_TEMP = "FAULT_DEVICE_TEMP"
internal const val FAULT_FWD_HARD_LIM = "FAULT_FWD_HARD_LIMI"
internal const val FAULT_FWD_SOFT_LIM = "FAULT_FWD_SOFT_LIM"
internal const val FAULT_FUSED_SENSE_SYNC = "FAULT_FUSED_SENSE_SYNC"
internal const val FAULT_HARDWARE = "FAULT_HARDWARE"
internal const val FAULT_MISSING_REMOTE = "FAULT_MISSING_REMOTE"
internal const val FAULT_OVERVOLT_SUPP = "FAULT_OVERVOLT_SUPP"
internal const val FAULT_PROC_TEMP = "FAULT_PROC_TEMP"
internal const val FAULT_REV_HARD_LIM = "FAULT_REV_HARD_LIM"
internal const val FAULT_REV_SOFT_LIM = "FAULT_REV_SOFT_LIM"
internal const val FAULT_STATOR_CURR_LIM = "FAULT_STATOR_CURR_LIM"
internal const val FAULT_SUPPLY_CURR_LIM = "FAULT_SUPPLY_CURR_LIM"
internal const val FAULT_UNDERVOLT_SUPP = "FAULT_UNDERVOLT_SUPP"
internal const val FAULT_UNLICENSED = "FAULT_UNLICENSED"
internal const val FAULT_UNSTABLE_SUPPV = "FAULT_UNSTABLE_SUPPV"
internal const val FAULT_FUSED_NO_LIC = "FAULT_FUSED_NO_LIC"
internal const val FWD_LIM = "FWD_LIM"
internal const val MOTION_MAGIC_RUNNING = "MOTION_MAGIC_RUNNING"
internal const val PROCESSOR_TEMP = "PROCESSOR_TEMP"
internal const val REV_LIM = "REV_LIM"
internal const val ROTOR_POS = "ROTOR_POS"
internal const val ROTOR_VEL = "ROTOR_VEL"
internal const val SIM_STATE = "SIM_STATE"
internal const val STICKY_FAULT_BOOT_EN = "STICKY_FAULT_BOOT_EN"
internal const val STICKY_FAULT_TEMP = "STICKY_FAULT_TEMP"
internal const val STICKY_FAULT_FWD_HARD_LIM = "STICKY_FAULT_FWD_HARD_LIM"
internal const val STICKY_FAULT_FWD_SOFT_LIM = "STICKY_FAULT_FWD_SOFT_LIM"
internal const val STICKY_FAULT_FUSED_SENSE_SYNC = "STICKY_FAULT_FUSED_SENSE_SYNC"
internal const val STICKY_FAULT_HARDWARE = "STICKY_FAULT_HARDWARE"
internal const val STICKY_FAULT_MISSING_REMOTE = "STICKY_FAULT_MISSING_REMOTE"
internal const val STICKY_FAULT_OVERVOLT_SUPP = "STICKY_FAULT_OVERVOLT_SUPP"
internal const val STICKY_FAULT_PROC_TEMP = "STICKY_FAULT_PROC_TEMP"
internal const val STICKY_FAULT_REV_HARD_LIM = "STICKY_FAULT_REV_HARD_LIM"
internal const val STICKY_FAULT_REV_SOFT_LIM = "STICKY_FAULT_REV_SOFT_LIM"
internal const val STICKY_FAULT_STATOR_CURR_LIM = "STICKY_FAULT_STATOR_CURR_LIM"
internal const val STICKY_FAULT_SUPPLY_CURR_LIM = "STICKY_FAULT_SUPPLY_CURR_LIM"
internal const val STICKY_FAULT_UNDERVOLT_SUPP = "STICKY_FAULT_UNDERVOLT_SUPP"
internal const val STICKY_FAULT_UNLICENSED = "STICKY_FAULT_UNLICENSED"
internal const val STICKY_FAULT_UNSTABLE_SUPPV = "STICKY_FAULT_UNSTABLE_SUPPV"
internal const val STICKY_FAULT_FUSED_NO_LIC = "STICKY_FAULT_FUSED_NO_LIC"
internal const val SUPPLY_VOLTAGE = "SUPPLY_VOLTAGE"
internal const val TORQUE_CURRENT = "TORQUE_CURRENT"
internal const val VELOCITY = "VELOCITY"
internal const val VERSION = "VERSION"
internal const val VERSION_BUG = "VERSION_BUG"
internal const val VERSION_BUILD = "VERSION_BUILD"
internal const val VERSION_MAJOR = "VERSION_MAJOR"
internal const val VERSION_MINOR = "VERSION_MINOR"
internal const val HAS_RESET_OCCURRED = "HAS_RESET_OCCURRED"
internal const val APPLIED_CONTROL = "APPLIED_CONTROL"
internal const val IS_INVERTED = "IS_INVERTED"
internal const val IS_ALIVE = "IS_ALIVE"
internal const val IS_SAFETY_ENABLED = "IS_SAFETY_ENABLED"



class TalonFX6Measureable @JvmOverloads constructor(
    private val talonFX: TalonFX,
    override val description: String = "TalonFX ${talonFX.deviceID}"
): Measurable {

    override val deviceId = talonFX.deviceID
    override val measures = setOf(
        Measure(APPLIED_ROTOR_POLARITY, "Applied Rotor Polarity Clockwise") {talonFX.appliedRotorPolarity.value.value.toDouble()},
        Measure(BRIDGE_OUTPUT_COAST, "Bridge Output Coast"){if (talonFX.bridgeOuput.value.value == 0) 1.0 else 0.0 },
        Measure(BRIDGE_OUTPUT_BRAKE, "Bridge Output Brake"){if (talonFX.bridgeOuput.value.value == 1) 1.0 else 0.0},
        Measure(BRIDGE_OUTPUT_TRAPZ, "Bridge Output Trapeezoidal"){if (talonFX.bridgeOuput.value.value == 6) 1.0 else 0.0},
        Measure(BRIDGE_OUTPUT_FOC_TORQE, "Bridge Output FOC Torque"){if (talonFX.bridgeOuput.value.value == 7) 1.0 else 0.0},
        Measure(BRIDGE_OUTPUT_MUSIC, "Bridge Output Music Tone"){if (talonFX.bridgeOuput.value.value == 8) 1.0 else 0.0},
        Measure(BRIDGE_OUTPUT_FOC_EASY, "Bridge Output FOC Easy"){if (talonFX.bridgeOuput.value.value == 9) 1.0 else 0.0},
        Measure(BRIDGE_OUTPUT_FAULT_BRAKE, "Bridge Output Fault Brake"){if (talonFX.bridgeOuput.value.value == 12) 1.0 else 0.0},
        Measure(BRIDGE_OUTPUT_FAULT_COAST, "Bridge Output Fault Coast"){if (talonFX.bridgeOuput.value.value == 13) 1.0 else 0.0},
        Measure(CLOSED_LOOP_DERIVATIVE_OUTPUT, "Closed Loop Derivative Output"){talonFX.closedLoopDerivativeOutput.value},
        Measure(CLOSED_LOOP_ERROR, "Closed Loop Error"){talonFX.closedLoopError.value},
        Measure(CLOSED_LOOP_FEED_FWD, "Closed Loop Feed Forward"){talonFX.closedLoopFeedForward.value},
        Measure(CLOSED_LOOP_INTEGRATED_OUTPUT, "Closed Loop Integrated Output"){talonFX.closedLoopIntegratedOutput.value},
        Measure(CLOSED_LOOP_OUTPUT, "Closed Loop Output"){talonFX.closedLoopError.value},
        Measure(CLOSED_LOOP_PROPORTIONAL_OUTPUT, "Closed Loop Proportional Output"){talonFX.closedLoopProportionalOutput.value},
        Measure(CLOSED_LOOP_REFERENCE, "Closed Loop Reference"){talonFX.closedLoopReference.value},
        Measure(CLOSED_LOOP_REFERENCE_SLOPE, "Closed Loop Reference Slope"){talonFX.closedLoopReferenceSlope.value},
        Measure(CLOSED_LOOP_SLOT, "Closed Loop Slot"){talonFX.closedLoopSlot.value.toDouble()},
        Measure(DEVICE_ENABLE, "Device Enabled"){talonFX.deviceEnable.value.value.toDouble()},
        Measure(DEVICE_TEMP, "Device Temperature C"){talonFX.deviceTemp.value},
        Measure(DUTY_CYCLE, "Applied Duty Cycle"){talonFX.dutyCycle.value},
        Measure(FAULT_BOOT_DURING_EN, "Fault: Boot During Enable"){talonFX.fault_BootDuringEnable.value.toDouble()},
        Measure(FAULT_DEVICE_TEMP, "Fault: Device Temp"){talonFX.fault_DeviceTemp.value.toDouble()},
        Measure(FAULT_FWD_HARD_LIM, "Fault: FWD Hard Limit"){talonFX.fault_ForwardHardLimit.value.toDouble()},
        Measure(FAULT_FWD_SOFT_LIM, "Fault: FWD Soft Limit"){talonFX.fault_ForwardSoftLimit.value.toDouble()},
        Measure(FAULT_FUSED_SENSE_SYNC, "Fault: Fused Sensor Out of Sync"){talonFX.fault_FusedSensorOutOfSync.value.toDouble()},
        Measure(FAULT_HARDWARE, "Fault: Hardware"){talonFX.fault_Hardware.value.toDouble()},
        Measure(FAULT_MISSING_REMOTE, "Fault: Missing Remote Sensor"){talonFX.fault_MissingRemoteSensor.value.toDouble()},
        Measure(FAULT_OVERVOLT_SUPP, "Fault: Overvoltage Suppply"){talonFX.fault_OverSupplyV.value.toDouble()},
        Measure(FAULT_PROC_TEMP, "Fault: Processor Temperature"){talonFX.fault_ProcTemp.value.toDouble()},
        Measure(FAULT_REV_HARD_LIM, "Fault: REV Hard Limit"){talonFX.fault_ReverseHardLimit.value.toDouble()},
        Measure(FAULT_REV_SOFT_LIM, "Fault: REV Soft Limit"){talonFX.fault_ReverseSoftLimit.value.toDouble()},
        Measure(FAULT_STATOR_CURR_LIM, "Fault: Stator Current Limit"){talonFX.fault_StatorCurrLimit.value.toDouble()},
        Measure(FAULT_SUPPLY_CURR_LIM, "Fault: Supply Current Limit"){talonFX.fault_SupplyCurrLimit.value.toDouble()},
        Measure(FAULT_UNDERVOLT_SUPP, "Fault: Undervoltage Supply"){talonFX.fault_Undervoltage.value.toDouble()},
        Measure(FAULT_UNLICENSED, "Fault: Unlicensed Feature"){talonFX.fault_UnlicensedFeatureInUse.value.toDouble()},
        Measure(FAULT_UNSTABLE_SUPPV, "Fault: Unstable Supply Voltage"){talonFX.fault_UnstableSupplyV.value.toDouble()},
        Measure(FAULT_FUSED_NO_LIC, "Fault: Fused CANcoder No License"){talonFX.fault_UsingFusedCANcoderWhileUnlicensed.value.toDouble()},
        Measure(FWD_LIM, "Forward Limit Switch Closed"){if(talonFX.forwardLimit.value.value == 0) 1.0 else 0.0},
        Measure(MOTION_MAGIC_RUNNING, "Motion Magic Running"){talonFX.motionMagicIsRunning.value.value.toDouble()},
        Measure(POSITION, "Position"){talonFX.position.value},
        Measure(PROCESSOR_TEMP, "Processor Temp"){talonFX.processorTemp.value},
        Measure(REV_LIM, "Reverse Limit Switch Closed"){if(talonFX.reverseLimit.value.value == 0) 1.0 else 0.0},
        Measure(ROTOR_POS, "Rotor Position"){talonFX.rotorPosition.value},
        Measure(ROTOR_VEL, "Rotor Velocity"){talonFX.rotorVelocity.value},
        Measure(STATOR_CURRENT, "Stator Current"){talonFX.statorCurrent.value},
        Measure(SUPPLY_CURRENT, "Supply Current"){talonFX.supplyCurrent.value},
        Measure(SUPPLY_VOLTAGE, "Supply Voltage"){talonFX.supplyVoltage.value},
        Measure(TORQUE_CURRENT, "Torque Current"){talonFX.torqueCurrent.value},
        Measure(VELOCITY, "Velocity"){talonFX.velocity.value},
        Measure(HAS_RESET_OCCURRED, "Has Reset Occurred"){talonFX.hasResetOccurred().toDouble()},
        Measure(IS_INVERTED, "Is Inverted"){talonFX.inverted.toDouble()},
        Measure(IS_ALIVE, "Is Alive"){talonFX.isAlive.toDouble()},
        Measure(IS_SAFETY_ENABLED, "Is Safety Enabled"){talonFX.isSafetyEnabled.toDouble()},
        Measure(CLOSED_LOOP_TARGET, "Closed Loop Target"){
            var mode = talonFX.controlMode.value
            when(mode) {
                4 -> talonFX.appliedControl.controlInfo.nameValues.get("position").toDouble()//Position
                5 -> talonFX.appliedControl.controlInfo.nameValues.get("velocity").toDouble()//Velocity
                6 -> talonFX.appliedControl.controlInfo.nameValues.get("position").toDouble()//Motion Magic
                8 -> talonFX.appliedControl.controlInfo.nameValues.get("position").toDouble()//FOC Position DC
                9 -> talonFX.appliedControl.controlInfo.nameValues.get("velocity").toDouble()//FOC Velocity DC
                10 -> talonFX.appliedControl.controlInfo.nameValues.get("position").toDouble()//FOC Motion Magic DC
                12 -> talonFX.appliedControl.controlInfo.nameValues.get("position").toDouble()//Position V
                13 -> talonFX.appliedControl.controlInfo.nameValues.get("velocity").toDouble()//Velocity V
                14 -> talonFX.appliedControl.controlInfo.nameValues.get("position").toDouble()//Motion Magic V
                15 -> talonFX.appliedControl.controlInfo.nameValues.get("output").toDouble()//FOC V
                16 -> talonFX.appliedControl.controlInfo.nameValues.get("position").toDouble()//FOC Pos V
                17 -> talonFX.appliedControl.controlInfo.nameValues.get("velocity").toDouble()//FOC Vel V
                18 -> talonFX.appliedControl.controlInfo.nameValues.get("position").toDouble()//FOC Motion Magic V
                19 -> talonFX.appliedControl.controlInfo.nameValues.get("output").toDouble()//FOC Torque I
                20 -> talonFX.appliedControl.controlInfo.nameValues.get("position").toDouble()//FOC Pos Torque I
                21 -> talonFX.appliedControl.controlInfo.nameValues.get("velocity").toDouble()//FOC Vel Torque I
                22 -> talonFX.appliedControl.controlInfo.nameValues.get("position").toDouble()//FOC Motion Magic Torque I
                else -> 2767.0
            }
        }


    )
}