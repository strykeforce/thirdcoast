package org.strykeforce.telemetry.util

internal class TriangleSignalGenerator(
    frequency: Double, phase: Double, amplitude: Double, offset: Double, invert: Double
) : SignalGenerator(frequency, phase, amplitude, offset, invert) {

    override fun getValue(time: Double): Double {
        val t = frequency * time + phase
        val `val` = 2.0 * Math.abs(t - 2 * Math.floor(t / 2.0) - 1.0) - 1.0
        return invert * amplitude * `val` + offset
    }

    override fun toString(): String {
        return "Triangle at " + super.toString()
    }
}
