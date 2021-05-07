package org.strykeforce.telemetry.util

internal class SawtoothSignalGenerator(
    frequency: Double, phase: Double, amplitude: Double, offset: Double, invert: Double
) : SignalGenerator(frequency, phase, amplitude, offset, invert) {

    override fun getValue(time: Double): Double {
        val t = frequency * time + phase
        val `val` = 2.0 * (t - Math.floor(t + 0.5))
        return invert * amplitude * `val` + offset
    }

    override fun toString(): String {
        return "Sawtooth at " + super.toString()
    }
}
