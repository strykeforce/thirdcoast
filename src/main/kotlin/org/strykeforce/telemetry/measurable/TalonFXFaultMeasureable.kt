package org.strykeforce.telemetry.talon

import com.ctre.phoenix6.hardware.TalonFX
import org.strykeforce.telemetry.measurable.Measurable
import org.strykeforce.telemetry.measurable.Measure
import org.strykeforce.telemetry.measurable.toDouble

internal const val FAULT_BOOT_DURING_EN = "FAULT_BOOT_DURING_EN"
internal const val FAULT_BRIDGE_BROWNOUT = "FAULT_BRIDGEBROWNOUT"
internal const val FAULT_DEVICE_TEMP = "FAULT_DEVICE_TEMP"
internal const val FAULT_FWD_HARD_LIM = "FAULT_FWD_HARD_LIMI"
internal const val FAULT_FWD_SOFT_LIM = "FAULT_FWD_SOFT_LIM"
internal const val FAULT_FUSED_SENSE_SYNC = "FAULT_FUSED_SENSE_SYNC"
internal const val FAULT_HARDWARE = "FAULT_HARDWARE"
internal const val FAULT_MISSING_DIFF_FX = "FAULT_MISSING_DIFF_FX"
internal const val FAULT_OVERVOLT_SUPP = "FAULT_OVERVOLT_SUPP"
internal const val FAULT_PROC_TEMP = "FAULT_PROC_TEMP"
internal const val FAULT_REMOTE_SENSE_DATA_INVALID = "FAULT_REMOTE_SENSE_DATA_INVALID"
internal const val FAULT_REMOTE_SENSE_POS_OVERFLOW = "FAULT_REMOTE_SENSE_POS_OVERFLOW"
internal const val FAULT_REMOTE_SENSE_RESET = "FAULT_REMOTE_SENSE_RESET"
internal const val FAULT_REV_HARD_LIM = "FAULT_REV_HARD_LIM"
internal const val FAULT_REV_SOFT_LIM = "FAULT_REV_SOFT_LIM"
internal const val FAULT_STATOR_CURR_LIM = "FAULT_STATOR_CURR_LIM"
internal const val FAULT_SUPPLY_CURR_LIM = "FAULT_SUPPLY_CURR_LIM"
internal const val FAULT_UNDERVOLT_SUPP = "FAULT_UNDERVOLT_SUPP"
internal const val FAULT_UNLICENSED = "FAULT_UNLICENSED"
internal const val FAULT_UNSTABLE_SUPPV = "FAULT_UNSTABLE_SUPPV"
internal const val FAULT_FUSED_NO_LIC = "FAULT_FUSED_NO_LIC"

internal const val STICKY_FAULT_BOOT_EN = "STICKY_FAULT_BOOT_EN"
internal const val STICKY_FAULT_BRIDGE_BROWNOUT = "STICKY_FAULT_BRIDGE_BROWNOUT"
internal const val STICKY_FAULT_TEMP = "STICKY_FAULT_TEMP"
internal const val STICKY_FAULT_FWD_HARD_LIM = "STICKY_FAULT_FWD_HARD_LIM"
internal const val STICKY_FAULT_FWD_SOFT_LIM = "STICKY_FAULT_FWD_SOFT_LIM"
internal const val STICKY_FAULT_FUSED_SENSE_SYNC = "STICKY_FAULT_FUSED_SENSE_SYNC"
internal const val STICKY_FAULT_HARDWARE = "STICKY_FAULT_HARDWARE"
internal const val STICKY_FAULT_MISSING_REMOTE = "STICKY_FAULT_MISSING_REMOTE"
internal const val STICKY_FAULT_OVERVOLT_SUPP = "STICKY_FAULT_OVERVOLT_SUPP"
internal const val STICKY_FAULT_PROC_TEMP = "STICKY_FAULT_PROC_TEMP"
internal const val STICKY_FAULT_REMOTE_SENSE_DATA_INVALID = "STICKY_FAULT_REMOTE_SENSE_DATA_INVALID"
internal const val STICKY_FAULT_REMOTE_SENSE_POS_OVERFLOW = "STICKY_FAULT_REMOTE_SENSE_POS_OVERFLOW"
internal const val STICKY_FAULT_REMOTE_SENSE_RESET = "STICKY_FAULT_REMOTE_SENSE_RESET"
internal const val STICKY_FAULT_REV_HARD_LIM = "STICKY_FAULT_REV_HARD_LIM"
internal const val STICKY_FAULT_REV_SOFT_LIM = "STICKY_FAULT_REV_SOFT_LIM"
internal const val STICKY_FAULT_STATOR_CURR_LIM = "STICKY_FAULT_STATOR_CURR_LIM"
internal const val STICKY_FAULT_SUPPLY_CURR_LIM = "STICKY_FAULT_SUPPLY_CURR_LIM"
internal const val STICKY_FAULT_UNDERVOLT_SUPP = "STICKY_FAULT_UNDERVOLT_SUPP"
internal const val STICKY_FAULT_UNLICENSED = "STICKY_FAULT_UNLICENSED"
internal const val STICKY_FAULT_UNSTABLE_SUPPV = "STICKY_FAULT_UNSTABLE_SUPPV"
internal const val STICKY_FAULT_FUSED_NO_LIC = "STICKY_FAULT_FUSED_NO_LIC"


class TalonFXFaultMeasureable @JvmOverloads constructor(
    private val talonFX: TalonFX,
    override val description: String = "TalonFX Errors ${talonFX.deviceID}"
): Measurable {

    override val deviceId = talonFX.deviceID
    override val measures = setOf(
        Measure(FAULT_BOOT_DURING_EN, "Fault: Boot during Enable"){talonFX.fault_BootDuringEnable.valueAsDouble},
        Measure(FAULT_BRIDGE_BROWNOUT, "Fault: Bridge Brownout"){talonFX.fault_BridgeBrownout.valueAsDouble},
        Measure(FAULT_DEVICE_TEMP, "Fault: Device Temp"){talonFX.fault_DeviceTemp.valueAsDouble},
        Measure(FAULT_FWD_HARD_LIM, "Fault: FWD Hard Limit"){talonFX.fault_ForwardHardLimit.valueAsDouble},
        Measure(FAULT_FWD_SOFT_LIM, "Fault: FWD Soft Limit"){talonFX.fault_ForwardSoftLimit.valueAsDouble},
        Measure(FAULT_FUSED_SENSE_SYNC, "Fault: Fused Sensor Out of Sync"){talonFX.fault_FusedSensorOutOfSync.valueAsDouble},
        Measure(FAULT_HARDWARE, "Fault: Hardware"){talonFX.fault_Hardware.valueAsDouble},
        Measure(FAULT_MISSING_DIFF_FX, "Fault: Missing Differential FX"){talonFX.fault_MissingDifferentialFX.valueAsDouble},
        Measure(FAULT_OVERVOLT_SUPP, "Fault: OverVolt Supply"){ talonFX.fault_OverSupplyV.valueAsDouble},
        Measure(FAULT_PROC_TEMP, "Fault: Processor Temp"){talonFX.fault_ProcTemp.valueAsDouble},
        Measure(FAULT_REMOTE_SENSE_DATA_INVALID, "Fault: Remote Sensor Data Invalid"){talonFX.fault_RemoteSensorDataInvalid.valueAsDouble},
        Measure(FAULT_REMOTE_SENSE_POS_OVERFLOW, "Fault: Remote Sensor Pos Overflow"){talonFX.fault_RemoteSensorPosOverflow.valueAsDouble},
        Measure(FAULT_REMOTE_SENSE_RESET, "Fault: Remote Sensor Reset"){talonFX.fault_RemoteSensorReset.valueAsDouble},
        Measure(FAULT_REV_HARD_LIM, "Fault: REV Hard Limit"){talonFX.fault_ReverseHardLimit.valueAsDouble},
        Measure(FAULT_REV_SOFT_LIM, "Fault: REV Soft Limit"){talonFX.fault_ReverseSoftLimit.valueAsDouble},
        Measure(FAULT_STATOR_CURR_LIM, "Fault: Stator Current Limit"){talonFX.fault_StatorCurrLimit.valueAsDouble},
        Measure(FAULT_SUPPLY_CURR_LIM, "Fault: Supply Current Limit"){talonFX.fault_SupplyCurrLimit.valueAsDouble},
        Measure(FAULT_UNDERVOLT_SUPP, "Fault: Undervoltage"){talonFX.fault_Undervoltage.valueAsDouble},
        Measure(FAULT_UNLICENSED, "Fault: Unlicensed Feature Used"){talonFX.fault_UnlicensedFeatureInUse.valueAsDouble},
        Measure(FAULT_UNSTABLE_SUPPV, "Fault: Unstable Supply Voltage"){talonFX.fault_UnstableSupplyV.valueAsDouble},
        Measure(FAULT_FUSED_NO_LIC, "Fault: Fused Sensor no License"){talonFX.fault_UsingFusedCANcoderWhileUnlicensed.valueAsDouble},

        Measure(STICKY_FAULT_BOOT_EN, "Sticky: Boot during Enable"){talonFX.stickyFault_BootDuringEnable.valueAsDouble},
        Measure(STICKY_FAULT_BRIDGE_BROWNOUT, "Sticky: Bridge Brownout"){talonFX.stickyFault_BridgeBrownout.valueAsDouble},
        Measure(STICKY_FAULT_TEMP, "Sticky: Device Temp"){talonFX.stickyFault_DeviceTemp.valueAsDouble},
        Measure(STICKY_FAULT_FWD_HARD_LIM, "Sticky: FWD Hard Limit"){talonFX.stickyFault_ForwardHardLimit.valueAsDouble},
        Measure(STICKY_FAULT_FWD_SOFT_LIM, "Sticky: FWD Soft Limit"){talonFX.stickyFault_ForwardSoftLimit.valueAsDouble},
        Measure(STICKY_FAULT_FUSED_SENSE_SYNC, "Sticky: Fused Sensor Out of Sync"){talonFX.stickyFault_FusedSensorOutOfSync.valueAsDouble},
        Measure(STICKY_FAULT_HARDWARE, "Sticky: Hardware"){talonFX.stickyFault_Hardware.valueAsDouble},
        Measure(STICKY_FAULT_OVERVOLT_SUPP, "Sticky: Overvoltage Supply"){talonFX.stickyFault_OverSupplyV.valueAsDouble},
        Measure(STICKY_FAULT_PROC_TEMP, "Sticky: Processor Temp"){talonFX.stickyFault_ProcTemp.valueAsDouble},
        Measure(STICKY_FAULT_REMOTE_SENSE_DATA_INVALID, "Sticky: Remote Sensor Data Invalid"){talonFX.stickyFault_RemoteSensorDataInvalid.valueAsDouble},
        Measure(STICKY_FAULT_REMOTE_SENSE_POS_OVERFLOW, "Sticky: Remote Sensor Pos Overflow"){talonFX.stickyFault_RemoteSensorPosOverflow.valueAsDouble},
        Measure(STICKY_FAULT_REMOTE_SENSE_RESET, "Sticky: Remote Sensor Reset"){talonFX.stickyFault_RemoteSensorReset.valueAsDouble},
        Measure(STICKY_FAULT_REV_HARD_LIM, "Sticky: REV Hard Limit"){talonFX.stickyFault_ReverseHardLimit.valueAsDouble},
        Measure(STICKY_FAULT_REV_SOFT_LIM, "Sticky: REV Soft Limit"){talonFX.stickyFault_ReverseSoftLimit.valueAsDouble},
        Measure(STICKY_FAULT_STATOR_CURR_LIM, "Sticky: Stator Current Limit"){talonFX.stickyFault_StatorCurrLimit.valueAsDouble},
        Measure(STICKY_FAULT_SUPPLY_CURR_LIM, "Sticky: Supply Current Limit"){talonFX.stickyFault_SupplyCurrLimit.valueAsDouble},
        Measure(STICKY_FAULT_UNDERVOLT_SUPP, "Sticky: Undervoltage"){talonFX.stickyFault_Undervoltage.valueAsDouble},
        Measure(STICKY_FAULT_UNLICENSED, "Sticky: Unlicensed Feature Used"){talonFX.stickyFault_UnlicensedFeatureInUse.valueAsDouble},
        Measure(STICKY_FAULT_UNSTABLE_SUPPV, "Sticky: Unstable Supply Voltage"){talonFX.stickyFault_UnstableSupplyV.valueAsDouble},
        Measure(STICKY_FAULT_FUSED_NO_LIC, "Sticky: Fused Sensor No License"){talonFX.stickyFault_UsingFusedCANcoderWhileUnlicensed.valueAsDouble}
    )

    override fun equals(other: Any?) : Boolean {
        if(this === other) return  true
        if(javaClass != other?.javaClass) return  false

        other as TalonFXFaultMeasureable
        if(deviceId != other.deviceId) return false
        return true
    }

    override fun hashCode() = deviceId
}