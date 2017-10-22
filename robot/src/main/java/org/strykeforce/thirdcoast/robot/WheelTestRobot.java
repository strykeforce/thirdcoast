package org.strykeforce.thirdcoast.robot;

import com.electronwill.nightconfig.core.file.FileConfig;
import edu.wpi.first.wpilibj.IterativeRobot;
import org.strykeforce.thirdcoast.swerve.Wheel;
import org.strykeforce.thirdcoast.talon.TalonProvisioner;

/**
 * Third Coast test robot.
 */

public class WheelTestRobot extends IterativeRobot {

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
  private int testCaseIndex = 0;
  private boolean isTestCaseChanged = true;

  @Override
  public void robotInit() {
    FileConfig config = FileConfig.builder("/home/lvuser/thirdcoast.toml")
        .defaultResource("/org/strykeforce/thirdcoast/defaults.toml")
        .build();

    RobotComponent component = DaggerRobotComponent.builder().toml(config).build();
    controls = component.controls();
    TalonProvisioner provisioner = new TalonProvisioner(config);
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
