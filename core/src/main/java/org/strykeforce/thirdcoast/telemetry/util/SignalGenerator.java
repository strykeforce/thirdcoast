package org.strykeforce.thirdcoast.telemetry.util;

/**
 * Generate a signal used for simulation.
 */
public abstract class SignalGenerator {

  // for System.nanoTime() resolution
  private final static double TICKS_PER_SECOND = 1e9;

  protected final double frequency;
  protected final double amplitude;
  protected final double offset;
  protected final double invert;
  protected final double phase;
  private long startTime = System.nanoTime();
  private double time;

  SignalGenerator(double frequency, double phase, double amplitude, double offset,
      double invert) {
    this.frequency = frequency;
    this.phase = phase;
    this.amplitude = amplitude;
    this.offset = offset;
    this.invert = invert;
  }

  abstract double getValue(double time);

  public double getValue() {
    time = (double) (System.nanoTime() - startTime) / TICKS_PER_SECOND;
    return getValue(time);
  }

  public double getTimeForLastValue() {
    return time;
  }

  public void reset() {
    startTime = System.nanoTime();
  }

  public boolean isInverted() {
    return invert == -1.0;
  }

  @Override
  public String toString() {
    return String
        .format("%.2f hz with amplitude %.2f, phase %.2f, offset %.2f%s", frequency, amplitude,
            phase, offset, isInverted() ? ", inverted" : "");

  }

  /**
   * Available signal types.
   */
  public enum SignalType {
    SINE, SQUARE, TRIANGLE, SAWTOOTH
  }

  /**
   * Builder for {@link SignalGenerator}.
   */
  public static class Builder {

    private final SignalType type;
    private double frequency = 1.0;
    private double phase = 0.0;
    private double amplitude = 1.0;
    private double offset = 0.0;
    private double invert = 1.0;

    public Builder(SignalType type) {
      this.type = type;
    }

    public Builder frequency(double val) {
      frequency = val;
      return this;
    }

    public Builder phase(double val) {
      phase = val;
      return this;
    }

    public Builder amplitude(double val) {
      amplitude = val;
      return this;
    }

    public Builder offset(double val) {
      offset = val;
      return this;
    }

    public Builder invert(boolean val) {
      invert = val ? -1.0 : 1.0;
      return this;
    }

    public SignalGenerator build() {
      switch (type) {

        case SINE:
          return new SineSignalGenerator(frequency, phase, amplitude, offset, invert);
        case SQUARE:
          return new SquareSignalGenerator(frequency, phase, amplitude, offset, invert);
        case TRIANGLE:
          return new TriangleSignalGenerator(frequency, phase, amplitude, offset, invert);
        case SAWTOOTH:
          return new SawtoothSignalGenerator(frequency, phase, amplitude, offset, invert);
      }
      return null;
    }
  }
}
