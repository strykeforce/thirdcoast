package org.strykeforce.thirdcoast.telemetry.util;

class SquareSignalGenerator extends SignalGenerator {

  public SquareSignalGenerator(
      double frequency, double phase, double amplitude, double offset, double invert) {
    super(frequency, phase, amplitude, offset, invert);
  }

  @Override
  double getValue(double time) {
    double t = frequency * time + phase;
    double val = Math.signum(Math.sin(2.0 * Math.PI * t));
    return invert * amplitude * val + offset;
  }

  @Override
  public String toString() {
    return "Square at " + super.toString();
  }
}
