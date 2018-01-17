package org.strykeforce.thirdcoast.telemetry.util;

class SawtoothSignalGenerator extends SignalGenerator {

  public SawtoothSignalGenerator(
      double frequency, double phase, double amplitude, double offset, double invert) {
    super(frequency, phase, amplitude, offset, invert);
  }

  @Override
  double getValue(double time) {
    double t = frequency * time + phase;
    double val = 2.0 * (t - Math.floor(t + 0.5));
    return invert * amplitude * val + offset;
  }

  @Override
  public String toString() {
    return "Sawtooth at " + super.toString();
  }
}
