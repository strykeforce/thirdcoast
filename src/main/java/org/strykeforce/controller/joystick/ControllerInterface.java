package org.strykeforce.controller.joystick;

public interface ControllerInterface {

  /**
   * @return current value of the FWD stick
   */
  public double getFwd();

  /**
   * @return current value of the STRAFE stick
   */
  public double getStr();

  /**
   * @return current value of the YAW stick
   */
  public double getYaw();
}
