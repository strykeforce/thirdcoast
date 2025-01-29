package org.strykeforce.telemetry.measurable

import com.ctre.phoenix6.hardware.TalonFXS
import org.strykeforce.telemetry.talon.*
import org.strykeforce.telemetry.talon.FAULT_BOOT_DURING_EN
import org.strykeforce.telemetry.talon.FAULT_BRIDGE_BROWNOUT
import org.strykeforce.telemetry.talon.FAULT_DEVICE_TEMP
import org.strykeforce.telemetry.talon.FAULT_FUSED_NO_LIC
import org.strykeforce.telemetry.talon.FAULT_FUSED_SENSE_SYNC
import org.strykeforce.telemetry.talon.FAULT_FWD_HARD_LIM
import org.strykeforce.telemetry.talon.FAULT_FWD_SOFT_LIM
import org.strykeforce.telemetry.talon.FAULT_HARDWARE
import org.strykeforce.telemetry.talon.FAULT_MISSING_DIFF_FX
import org.strykeforce.telemetry.talon.FAULT_OVERVOLT_SUPP
import org.strykeforce.telemetry.talon.FAULT_PROC_TEMP
import org.strykeforce.telemetry.talon.FAULT_REMOTE_SENSE_DATA_INVALID
import org.strykeforce.telemetry.talon.FAULT_REMOTE_SENSE_POS_OVERFLOW
import org.strykeforce.telemetry.talon.FAULT_REMOTE_SENSE_RESET
import org.strykeforce.telemetry.talon.FAULT_REV_HARD_LIM
import org.strykeforce.telemetry.talon.FAULT_REV_SOFT_LIM
import org.strykeforce.telemetry.talon.FAULT_STATOR_CURR_LIM
import org.strykeforce.telemetry.talon.FAULT_SUPPLY_CURR_LIM
import org.strykeforce.telemetry.talon.FAULT_UNDERVOLT_SUPP
import org.strykeforce.telemetry.talon.FAULT_UNLICENSED
import org.strykeforce.telemetry.talon.FAULT_UNSTABLE_SUPPV
import org.strykeforce.telemetry.talon.STICKY_FAULT_BOOT_EN
import org.strykeforce.telemetry.talon.STICKY_FAULT_BRIDGE_BROWNOUT
import org.strykeforce.telemetry.talon.STICKY_FAULT_FUSED_NO_LIC
import org.strykeforce.telemetry.talon.STICKY_FAULT_FUSED_SENSE_SYNC
import org.strykeforce.telemetry.talon.STICKY_FAULT_FWD_HARD_LIM
import org.strykeforce.telemetry.talon.STICKY_FAULT_FWD_SOFT_LIM
import org.strykeforce.telemetry.talon.STICKY_FAULT_HARDWARE
import org.strykeforce.telemetry.talon.STICKY_FAULT_OVERVOLT_SUPP
import org.strykeforce.telemetry.talon.STICKY_FAULT_PROC_TEMP
import org.strykeforce.telemetry.talon.STICKY_FAULT_REMOTE_SENSE_DATA_INVALID
import org.strykeforce.telemetry.talon.STICKY_FAULT_REMOTE_SENSE_POS_OVERFLOW
import org.strykeforce.telemetry.talon.STICKY_FAULT_REMOTE_SENSE_RESET
import org.strykeforce.telemetry.talon.STICKY_FAULT_REV_HARD_LIM
import org.strykeforce.telemetry.talon.STICKY_FAULT_REV_SOFT_LIM
import org.strykeforce.telemetry.talon.STICKY_FAULT_STATOR_CURR_LIM
import org.strykeforce.telemetry.talon.STICKY_FAULT_SUPPLY_CURR_LIM
import org.strykeforce.telemetry.talon.STICKY_FAULT_TEMP
import org.strykeforce.telemetry.talon.STICKY_FAULT_UNDERVOLT_SUPP
import org.strykeforce.telemetry.talon.STICKY_FAULT_UNLICENSED
import org.strykeforce.telemetry.talon.STICKY_FAULT_UNSTABLE_SUPPV

internal const val FAULT_BRIDGE_SHORT = "FAULT_BRIDGE_SHORT"
internal const val FAULT_DRIVE_HALL_DISABLE = "FAULT_DRIVE_HALL_DIABLE"
internal const val FAULT_HALL_MISSING = "FAULT_HALL_MISSING"
internal const val FAULT_HARD_REMOTE_MISSING = "FAULT_HARD_REMOTE_MISSING"
internal const val FAULT_SOFT_REMOTE_MISSING = "FAULT_SOFT_REMOTE_MISSING"
internal const val FAULT_MOTOR_TEMP_MISSING = "FAULT_MOTOR_TEMP_MISSING"
internal const val FAULT_MOTOR_TOO_HOT = "FAULT_MOTOR_TOO_HOT"
internal const val FAULT_STATIC_BRAKE_DISABLE = "FAULT_STATIC_BRAKE_DISABLE"
class TalonFXSFaultMeasureable @JvmOverloads constructor(
    private val talonFXS: TalonFXS,
    override val description: String = "TalonFXS Errors ${talonFXS.deviceID}"
): Measurable {

    override val deviceId = talonFXS.deviceID
    override val measures = setOf(
        Measure(FAULT_BOOT_DURING_EN, "Fault: Boot during Enable"){talonFXS.fault_BootDuringEnable.valueAsDouble},
        Measure(FAULT_BRIDGE_BROWNOUT, "Fault: Bridge Brownout"){talonFXS.fault_BridgeBrownout.valueAsDouble},
        Measure(FAULT_BRIDGE_SHORT, "Fault: Bridge Short"){talonFXS.fault_BridgeShort.valueAsDouble},
        Measure(FAULT_DEVICE_TEMP, "Fault: Device Temp"){talonFXS.fault_DeviceTemp.valueAsDouble},
        Measure(FAULT_DRIVE_HALL_DISABLE, "Fault: Drive Hall Disable"){talonFXS.fault_DriveDisabledHallSensor.valueAsDouble},
        Measure(FAULT_FWD_HARD_LIM, "Fault: FWD Hard Limit"){talonFXS.fault_ForwardHardLimit.valueAsDouble},
        Measure(FAULT_FWD_SOFT_LIM, "Fault: FWD Soft Limit"){talonFXS.fault_ForwardSoftLimit.valueAsDouble},
        Measure(FAULT_FUSED_SENSE_SYNC, "Fault: Fused Sensor Out of Sync"){talonFXS.fault_FusedSensorOutOfSync.valueAsDouble},
        Measure(FAULT_HARDWARE, "Fault: Hardware"){talonFXS.fault_Hardware.valueAsDouble},
        Measure(FAULT_HALL_MISSING, "Fault: Hall Missing"){talonFXS.fault_HallSensorMissing.valueAsDouble},
        Measure(FAULT_MISSING_DIFF_FX, "Fault: Missing Differential FX"){talonFXS.fault_MissingDifferentialFX.valueAsDouble},
        Measure(FAULT_HARD_REMOTE_MISSING, "Fault: Missing Remote Hard Lim"){talonFXS.fault_MissingHardLimitRemote.valueAsDouble},
        Measure(FAULT_SOFT_REMOTE_MISSING, "Fault: MIssing Remote Soft Lim"){talonFXS.fault_MissingSoftLimitRemote.valueAsDouble},
        Measure(FAULT_MOTOR_TEMP_MISSING, "Fault: Motor Temp Missing"){talonFXS.fault_MotorTempSensorMissing.valueAsDouble},
        Measure(FAULT_MOTOR_TOO_HOT, "Fault: Motor Too Hot"){talonFXS.fault_MotorTempSensorTooHot.valueAsDouble},
        Measure(FAULT_OVERVOLT_SUPP, "Fault: OverVolt Supply"){ talonFXS.fault_OverSupplyV.valueAsDouble},
        Measure(FAULT_PROC_TEMP, "Fault: Processor Temp"){talonFXS.fault_ProcTemp.valueAsDouble},
        Measure(FAULT_REMOTE_SENSE_DATA_INVALID, "Fault: Remote Sensor Data Invalid"){talonFXS.fault_RemoteSensorDataInvalid.valueAsDouble},
        Measure(FAULT_REMOTE_SENSE_POS_OVERFLOW, "Fault: Remote Sensor Pos Overflow"){talonFXS.fault_RemoteSensorPosOverflow.valueAsDouble},
        Measure(FAULT_REMOTE_SENSE_RESET, "Fault: Remote Sensor Reset"){talonFXS.fault_RemoteSensorReset.valueAsDouble},
        Measure(FAULT_REV_HARD_LIM, "Fault: REV Hard Limit"){talonFXS.fault_ReverseHardLimit.valueAsDouble},
        Measure(FAULT_REV_SOFT_LIM, "Fault: REV Soft Limit"){talonFXS.fault_ReverseSoftLimit.valueAsDouble},
        Measure(FAULT_STATIC_BRAKE_DISABLE, "Fault: Static Brake Diable"){talonFXS.fault_StaticBrakeDisabled.valueAsDouble},
        Measure(FAULT_STATOR_CURR_LIM, "Fault: Stator Current Limit"){talonFXS.fault_StatorCurrLimit.valueAsDouble},
        Measure(FAULT_SUPPLY_CURR_LIM, "Fault: Supply Current Limit"){talonFXS.fault_SupplyCurrLimit.valueAsDouble},
        Measure(FAULT_UNDERVOLT_SUPP, "Fault: Undervoltage"){talonFXS.fault_Undervoltage.valueAsDouble},
        Measure(FAULT_UNLICENSED, "Fault: Unlicensed Feature Used"){talonFXS.fault_UnlicensedFeatureInUse.valueAsDouble},
        Measure(FAULT_UNSTABLE_SUPPV, "Fault: Unstable Supply Voltage"){talonFXS.fault_UnstableSupplyV.valueAsDouble},
        Measure(FAULT_FUSED_NO_LIC, "Fault: Fused Sensor no License"){talonFXS.fault_UsingFusedCANcoderWhileUnlicensed.valueAsDouble},

        Measure(STICKY_FAULT_BOOT_EN, "Sticky: Boot during Enable"){talonFXS.stickyFault_BootDuringEnable.valueAsDouble},
        Measure(STICKY_FAULT_BRIDGE_BROWNOUT, "Sticky: Bridge Brownout"){talonFXS.stickyFault_BridgeBrownout.valueAsDouble},
        Measure(STICKY_FAULT_TEMP, "Sticky: Device Temp"){talonFXS.stickyFault_DeviceTemp.valueAsDouble},
        Measure(STICKY_FAULT_FWD_HARD_LIM, "Sticky: FWD Hard Limit"){talonFXS.stickyFault_ForwardHardLimit.valueAsDouble},
        Measure(STICKY_FAULT_FWD_SOFT_LIM, "Sticky: FWD Soft Limit"){talonFXS.stickyFault_ForwardSoftLimit.valueAsDouble},
        Measure(STICKY_FAULT_FUSED_SENSE_SYNC, "Sticky: Fused Sensor Out of Sync"){talonFXS.stickyFault_FusedSensorOutOfSync.valueAsDouble},
        Measure(STICKY_FAULT_HARDWARE, "Sticky: Hardware"){talonFXS.stickyFault_Hardware.valueAsDouble},
        Measure(STICKY_FAULT_OVERVOLT_SUPP, "Sticky: Overvoltage Supply"){talonFXS.stickyFault_OverSupplyV.valueAsDouble},
        Measure(STICKY_FAULT_PROC_TEMP, "Sticky: Processor Temp"){talonFXS.stickyFault_ProcTemp.valueAsDouble},
        Measure(STICKY_FAULT_REMOTE_SENSE_DATA_INVALID, "Sticky: Remote Sensor Data Invalid"){talonFXS.stickyFault_RemoteSensorDataInvalid.valueAsDouble},
        Measure(STICKY_FAULT_REMOTE_SENSE_POS_OVERFLOW, "Sticky: Remote Sensor Pos Overflow"){talonFXS.stickyFault_RemoteSensorPosOverflow.valueAsDouble},
        Measure(STICKY_FAULT_REMOTE_SENSE_RESET, "Sticky: Remote Sensor Reset"){talonFXS.stickyFault_RemoteSensorReset.valueAsDouble},
        Measure(STICKY_FAULT_REV_HARD_LIM, "Sticky: REV Hard Limit"){talonFXS.stickyFault_ReverseHardLimit.valueAsDouble},
        Measure(STICKY_FAULT_REV_SOFT_LIM, "Sticky: REV Soft Limit"){talonFXS.stickyFault_ReverseSoftLimit.valueAsDouble},
        Measure(STICKY_FAULT_STATOR_CURR_LIM, "Sticky: Stator Current Limit"){talonFXS.stickyFault_StatorCurrLimit.valueAsDouble},
        Measure(STICKY_FAULT_SUPPLY_CURR_LIM, "Sticky: Supply Current Limit"){talonFXS.stickyFault_SupplyCurrLimit.valueAsDouble},
        Measure(STICKY_FAULT_UNDERVOLT_SUPP, "Sticky: Undervoltage"){talonFXS.stickyFault_Undervoltage.valueAsDouble},
        Measure(STICKY_FAULT_UNLICENSED, "Sticky: Unlicensed Feature Used"){talonFXS.stickyFault_UnlicensedFeatureInUse.valueAsDouble},
        Measure(STICKY_FAULT_UNSTABLE_SUPPV, "Sticky: Unstable Supply Voltage"){talonFXS.stickyFault_UnstableSupplyV.valueAsDouble},
        Measure(STICKY_FAULT_FUSED_NO_LIC, "Sticky: Fused Sensor No License"){talonFXS.stickyFault_UsingFusedCANcoderWhileUnlicensed.valueAsDouble}
    )

    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(javaClass != other?.javaClass) return  false
        other as TalonFXSFaultMeasureable
        if(deviceId != other.deviceId) return false
        return true
    }

    override fun hashCode() = deviceId
}