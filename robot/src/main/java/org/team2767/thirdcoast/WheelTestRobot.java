package org.team2767.thirdcoast;

import edu.wpi.first.wpilibj.IterativeRobot;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.strykeforce.thirdcoast.swerve.Wheel;
import org.strykeforce.thirdcoast.talon.Talons;
import org.strykeforce.thirdcoast.util.Settings;

/** Third Coast test robot. */
public class WheelTestRobot extends IterativeRobot {

  private final double[][] testCases =
      new double[][] {
        {0, 0},
        {0, 0.2},
        {0.5, 0.2},
        {-0.5, 0.2},
        {0.1, 0.2},
        {0.4, 0.2},
        {-0.4, 0.2},
        {-0.2, 0.2},
        {0, 0.2},
        {0.2, 0.2},
        {0.4, 0.2},
        {0.5, 0.2},
        {-0.4, 0.2},
        {-0.2, 0.2}
      };
  private Wheel wheel;
  private Controls controls;
  private final Trigger upButton =
      new Trigger() {
        @Override
        public boolean get() {
          return controls.getGamepadYButton();
        }
      };
  private final Trigger downButton =
      new Trigger() {
        @Override
        public boolean get() {
          return controls.getGamepadAButton();
        }
      };
  private int testCaseIndex = 0;
  private boolean isTestCaseChanged = true;

  @Override
  public void robotInit() {
    URL config = null;
    try {
      config = new File("/home/lvuser/thirdcoast.toml").toURI().toURL();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    RobotComponent component = DaggerRobotComponent.builder().config(config).build();
    controls = component.controls();
    Talons talons = component.talons();
    wheel = new Wheel(talons, new Settings(), 1);
    wheel.setAzimuthZero(1000);
  }

  @Override
  public void teleopInit() {}

  private void logTestCase(double[] tc) {
    //    System.out.printf(
    //        "azimuth = %f, speed = %f, actual azimuth = %f, reversed = %b%n",
    //        tc[0] * wheel.getTicksPerRevolution(),
    //        tc[1],
    //        wheel.getAzimuthSetpoint(),
    //        wheel.isDriveReversed());
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
