package org.strykeforce.sidewinder.robot;

import edu.wpi.first.wpilibj.Joystick;

public class Controls {

  private static final int DRIVER_RIGHT_X_AXIS = 0;
  private static final int DRIVER_RIGHT_Y_AXIS = 1;
  private static final int DRIVER_LEFT_Y_AXIS = 2;
  private static final int DRIVER_TUNER_AXIS = 3;
  private static final int DRIVER_LEFT_X_AXIS = 4;

  private static final int DRIVER_LEFT_BUTTON = 1;
  private static final int DRIVER_RIGHT_SHOULDER_BUTTON = 2;
  private static final int DRIVER_RESET_BUTTON = 3;
  private static final int DRIVER_LEFT_SHOULDER_DOWN_BUTTON = 4;
  private static final int DRIVER_LEFT_SHOULDER_UP_BUTTON = 5;

  private static final int GAME_A_BUTTON = 1;
  private static final int GAME_B_BUTTON = 2;
  private static final int GAME_X_BUTTON = 3;
  private static final int GAME_Y_BUTTON = 4;
  private static final int GAME_LEFT_SHOULDER_BUTTON = 5;
  private static final int GAME_RIGHT_SHOULDER_BUTTON = 6;
  private static final int GAME_BACK_BUTTON = 7;
  private static final int GAME_START_BUTTON = 8;
  private static final int GAME_LEFT_STICK_BUTTON = 9;
  private static final int GAME_RIGHT_STICK_BUTTON = 10;

  private static final Controls instance = new Controls();

  private final Joystick gameController = new Joystick(0);
  private final Joystick driverController = new Joystick(1);

  private Controls() {
  }

  /**
   * Return the Controls singleton.
   *
   * @return the singleton instance
   */
  public static Controls getInstance() {
    return instance;
  }

  /**
   * Return the driver controller left stick Y-axis position.
   *
   * @return the position, range is -1.0 (full reverse) to 1.0 (full forward)
   */
  public double getForward() {
    return -driverController.getRawAxis(DRIVER_LEFT_Y_AXIS);
  }

  /**
   * Return the driver controller left stick X-axis position.
   *
   * @return the position, range is -1.0 (full left) to 1.0 (full right)
   */
  public double getStrafe() {
    return driverController.getRawAxis(DRIVER_LEFT_X_AXIS);
  }

  /**
   * Return the driver controller right stick X-axis position.
   *
   * * @return  the position, range is -1.0 (full left) to 1.0 (full right)
   */
  public double getAzimuth() {
    return driverController.getRawAxis(DRIVER_RIGHT_X_AXIS);
  }

  /**
   * Return the "Ch 6. Flaps Gain" knob value.
   *
   * @return the know position
   */
  public double getTuner() {return driverController.getRawAxis(DRIVER_TUNER_AXIS);}
}
