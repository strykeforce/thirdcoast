package org.strykeforce.swerve;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModuleState;

/** The default odometry strategy that wraps a {@code SwerveDriveOdometry}. */
public class KinematicOdometryStrategy implements OdometryStrategy {

  private final SwerveDriveOdometry odometry;

  public KinematicOdometryStrategy(
      SwerveDriveKinematics kinematics, Rotation2d gyroAngle, Pose2d initialPose) {
    this.odometry = new SwerveDriveOdometry(kinematics, gyroAngle, initialPose);
  }

  public KinematicOdometryStrategy(SwerveDriveKinematics kinematics, Rotation2d gyroAngle) {
    this(kinematics, gyroAngle, new Pose2d());
  }

  @Override
  public Pose2d getPoseMeters() {
    return odometry.getPoseMeters();
  }

  @Override
  public void resetPosition(Pose2d pose, Rotation2d gyroAngle) {
    odometry.resetPosition(pose, gyroAngle);
  }

  @Override
  public Pose2d update(Rotation2d gyroAngle, SwerveModuleState... moduleStates) {
    return odometry.update(gyroAngle, moduleStates);
  }

  @Override
  public Pose2d updateWithTime(
      double currentTimeSeconds, Rotation2d gyroAngle, SwerveModuleState... moduleStates) {
    return odometry.updateWithTime(currentTimeSeconds, gyroAngle, moduleStates);
  }
}
