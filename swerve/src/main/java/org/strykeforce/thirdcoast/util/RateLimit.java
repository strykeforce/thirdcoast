package org.strykeforce.thirdcoast.util;

/** Applies rate limit to joystick input. */
public class RateLimit {

  private final double rateLimit;
  private double lastLimit;

  public RateLimit(double rateLimit) {
    this.rateLimit = rateLimit;
  }

  /**
   * Return the joystick input adjusted to a rate limit.
   *
   * @param joystickInput joystick axis position
   * @return the joystick axis position after rate limiting
   */
  public double apply(double joystickInput) {
    double y;
    if (Math.abs(joystickInput - lastLimit) > rateLimit) {
      y = lastLimit + Math.copySign(rateLimit, joystickInput - lastLimit);
    } else {
      y = joystickInput;
    }

    lastLimit = y;
    return y;
  }
}
