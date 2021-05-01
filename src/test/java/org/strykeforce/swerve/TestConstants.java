package org.strykeforce.swerve;

import edu.wpi.first.wpilibj.geometry.Translation2d;

public class TestConstants {

  public final static double kDriveMotorOutputGear = 22;
  public final static double kDriveInputGear = 48;
  public final static double kBevelInputGear = 15;
  public final static double kBevelOutputGear = 45;
  public final static double kDriveGearRatio =
      (kDriveMotorOutputGear / kDriveInputGear) * (kBevelInputGear / kBevelOutputGear);
  public final static double kMaxSpeedMetersPerSecond = 3.84020432;
  public final static double kWheelDiameterInches = 3.0;

  public final static Translation2d[] kWheelLocations = new Translation2d[4];
  static {
    final double offset = 0.27305;
    kWheelLocations[0] = new Translation2d(offset, offset); // left front
    kWheelLocations[1] = new Translation2d(offset, -offset); // right front
    kWheelLocations[2] = new Translation2d(-offset, offset); // left rear
    kWheelLocations[3] = new Translation2d(-offset, -offset); // right rear
  }

}
