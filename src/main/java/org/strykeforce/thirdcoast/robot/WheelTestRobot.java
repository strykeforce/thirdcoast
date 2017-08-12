package org.strykeforce.thirdcoast.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import org.strykeforce.thirdcoast.swerve.Wheel;
import org.strykeforce.thirdcoast.talon.TalonParameters;

/**
 * Third Coast test robot.
 */

public class WheelTestRobot extends IterativeRobot {

  static {
    TalonParameters.register("/org/strykeforce/thirdcoast.toml");
  }

  private final Wheel wheel = new Wheel(0);
  private final Controls controls = Controls.getInstance();
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
  private double[][] testCases = new double[][]{
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
  private int testCaseIndex = 0;
  private boolean isTestCaseChanged = true;

  @Override
  public void robotInit() {
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
