package org.strykeforce.swerve;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;

/** The default odometry strategy that wraps a {@code SwerveDriveOdometry}. */
public class KinematicOdometryStrategy implements OdometryStrategy {

  private final SwerveDriveOdometry odometry;

  public KinematicOdometryStrategy(
      SwerveDriveKinematics kinematics,
      Pose2d initialPose,
      Rotation2d gyroAngle,
      SwerveModulePosition... swerveModulePositions) {
    this.odometry =
        new SwerveDriveOdometry(kinematics, gyroAngle, swerveModulePositions, initialPose);
  }

  public KinematicOdometryStrategy(
      SwerveDriveKinematics kinematics,
      Rotation2d gyroAngle,
      SwerveModulePosition... swerveModulePositions) {
    this(kinematics, new Pose2d(), gyroAngle, swerveModulePositions);
  }

  @Override
  public Pose2d getPoseMeters() {
    return odometry.getPoseMeters();
  }

  @Override
  public void resetPosition(
      Pose2d pose, Rotation2d gyroAngle, SwerveModulePosition... swerveModulePositions) {
    odometry.resetPosition(gyroAngle, swerveModulePositions, pose);
  }

  @Override
  public Pose2d update(Rotation2d gyroAngle, SwerveModulePosition... modulePositions) {
    return odometry.update(gyroAngle, modulePositions);
  }

  // @Override
  // public Pose2d updateWithTime(
  //     double currentTimeSeconds, Rotation2d gyroAngle, SwerveModuleState... moduleStates) {
  //   return odometry.updateWithTime(currentTimeSeconds, gyroAngle, moduleStates);
  // }
}
