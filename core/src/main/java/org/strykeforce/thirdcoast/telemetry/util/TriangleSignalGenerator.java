package org.strykeforce.thirdcoast.telemetry.util;

class TriangleSignalGenerator extends SignalGenerator {

  public TriangleSignalGenerator(double frequency, double phase, double amplitude, double offset,
      double invert) {
    super(frequency, phase, amplitude, offset, invert);
  }

  @Override
  double getValue(double time) {
    double t = frequency * time + phase;
    double val = 2.0 * Math.abs(t - 2 * Math.floor(t / 2.0) - 1.0) - 1.0;
    return invert * amplitude * val + offset;
  }

  @Override
  public String toString() {
    return "Triangle at " + super.toString();
  }
}
