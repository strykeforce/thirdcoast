package org.strykeforce.telemetry.measurable

import edu.wpi.first.wpilibj2.command.SubsystemBase

abstract class MeasurableSubsystem() : SubsystemBase(), Measurable {

    override val description = name

    val subsystemNumber: Int
        get() = name.hashCode()

    override val deviceId: Int
        get() = subsystemNumber

    override fun compareTo(other: Measurable): Int {
        val result = type.compareTo(other.type)
        return if (result != 0) result else deviceId.compareTo(other.deviceId)
    }
}