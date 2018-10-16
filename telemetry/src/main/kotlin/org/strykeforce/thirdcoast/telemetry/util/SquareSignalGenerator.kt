package org.strykeforce.thirdcoast.telemetry.util

internal class SquareSignalGenerator(
  frequency: Double, phase: Double, amplitude: Double, offset: Double, invert: Double
) : SignalGenerator(frequency, phase, amplitude, offset, invert) {

  override fun getValue(time: Double): Double {
    val t = frequency * time + phase
    val `val` = Math.signum(Math.sin(2.0 * Math.PI * t))
    return invert * amplitude * `val` + offset
  }

  override fun toString(): String {
    return "Square at " + super.toString()
  }
}
