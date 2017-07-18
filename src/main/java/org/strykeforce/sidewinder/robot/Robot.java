package org.strykeforce.sidewinder.robot;

import edu.wpi.first.wpilibj.IterativeRobot;

/**
 * Sidewinder test robot.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Robot extends IterativeRobot {

  private final Controls controls = Controls.getInstance();

  /**
   * Initialize the robot.
   */
  @Override
  public void robotInit() {
    System.out.println("Hello World!");
  }

  @Override
  public void teleopPeriodic() {
    System.out.printf("position = %f\n", controls.getForward());
  }


}
