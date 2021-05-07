package org.strykeforce.telemetry.util

internal class SineSignalGenerator(
    frequency: Double, phase: Double, amplitude: Double, offset: Double, invert: Double
) : SignalGenerator(frequency, phase, amplitude, offset, invert) {

    override fun getValue(time: Double): Double {
        val t = frequency * time + phase
        val `val` = Math.sin(2.0 * Math.PI * t)
        return invert * amplitude * `val` + offset
    }

    override fun toString(): String {
        return "Sine at " + super.toString()
    }
}
