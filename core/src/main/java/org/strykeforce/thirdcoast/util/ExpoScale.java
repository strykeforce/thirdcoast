package org.strykeforce.thirdcoast.util;

/** Applies exponential scaling and deadband to joystick inputs */
public class ExpoScale {

  private final double deadband;
  private final double scale;
  private final double offset;

  public ExpoScale(double deadband, double scale) {
    this.deadband = deadband;
    this.scale = scale;
    offset = 1.0 / (scale * Math.pow(1 - deadband, 3) + (1 - scale) * (1 - deadband));
  }

  public double applyExpoScale(double input) {
    double y;

    if (Math.abs(input) < deadband) {
      return 0;
    }

    y = input > 0 ? input - deadband : input + deadband;
    return (scale * Math.pow(y, 3) + (1 - scale) * y) * offset;
  }
}
