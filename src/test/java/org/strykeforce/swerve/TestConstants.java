package org.strykeforce.swerve;

import edu.wpi.first.wpilibj.geometry.Translation2d;

public class TestConstants {

  public static final double kDriveMotorOutputGear = 22;
  public static final double kDriveInputGear = 48;
  public static final double kBevelInputGear = 15;
  public static final double kBevelOutputGear = 45;
  public static final double kDriveGearRatio =
      (kDriveMotorOutputGear / kDriveInputGear) * (kBevelInputGear / kBevelOutputGear);
  public static final double kMaxSpeedMetersPerSecond = 3.84020432;
  public static final double kWheelDiameterInches = 3.0;

  public static final Translation2d[] kWheelLocations = new Translation2d[4];

  static {
    final double offset = 0.27305;
    kWheelLocations[0] = new Translation2d(offset, offset); // left front
    kWheelLocations[1] = new Translation2d(offset, -offset); // right front
    kWheelLocations[2] = new Translation2d(-offset, offset); // left rear
    kWheelLocations[3] = new Translation2d(-offset, -offset); // right rear
  }
}
