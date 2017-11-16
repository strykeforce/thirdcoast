package org.strykeforce.thirdcoast.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import org.strykeforce.thirdcoast.swerve.Wheel;
import org.strykeforce.thirdcoast.talon.TalonProvisioner;

/**
 * Third Coast test robot.
 */

public class WheelTestRobot extends IterativeRobot {

  private final double[][] testCases = new double[][]{
      {0, 0},
      {0, 2},
      {0.5, 2},
      {-0.5, 2},
      {0.1, 2},
      {0.4, 2},
      {-0.4, 2},
      {-0.2, 2},
      {0, 2},
      {0.2, 2},
      {0.4, 2},
      {0.5, 2},
      {-0.4, 2},
      {-0.2, 2}
  };
  private Wheel wheel;
  private Controls controls;
  private final Trigger upButton = new Trigger() {
    @Override
    public boolean get() {
      return controls.getGamepadYButton();
    }
  };
  private final Trigger downButton = new Trigger() {
    @Override
    public boolean get() {
      return controls.getGamepadAButton();
    }
  };
  private int testCaseIndex = 0;
  private boolean isTestCaseChanged = true;

  @Override
  public void robotInit() {
    RobotComponent component = DaggerRobotComponent.builder().config(Robot.CONFIG_FILE).build();
    controls = component.controls();
    TalonProvisioner provisioner = component.talonProvisioner();
    wheel = new Wheel(provisioner, 0);
    wheel.setAzimuthZero(2281);
  }

  @Override
  public void teleopInit() {
  }

  private void logTestCase(double[] tc) {
    System.out.printf("azimuth = %f, speed = %f, actual = %f, reversed = %b%n", tc[0], tc[1],
        wheel.getAzimuthSetpoint(), wheel.isDriveReversed());
  }

  @Override
  public void teleopPeriodic() {
    double[] testCase = testCases[testCaseIndex];
    wheel.set(testCase[0], testCase[1]);
    if (isTestCaseChanged) {
      logTestCase(testCases[testCaseIndex]);
      isTestCaseChanged = false;
    }
    if (upButton.hasActivated() && (testCaseIndex < testCases.length - 1)) {
      testCaseIndex++;
      isTestCaseChanged = true;
    }
    if (downButton.hasActivated() && (testCaseIndex != 0)) {
      testCaseIndex--;
      isTestCaseChanged = true;
    }
  }

  @Override
  public void disabledInit() {
    wheel.stop();
  }

  @Override
  public void disabledPeriodic() {
    wheel.stop();
  }
}
