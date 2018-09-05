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

  /**
   * Return the joystick axis position input adjusted on an exponential scale with deadband
   * adjustment.
   *
   * @param input the joystick axis position
   * @return the adjusted input value, range is -1.0 to 1.0
   */
  public double apply(double input) {
    double y;

    if (Math.abs(input) < deadband) {
      return 0;
    }

    y = input > 0 ? input - deadband : input + deadband;
    return (scale * Math.pow(y, 3) + (1 - scale) * y) * offset;
  }
}
