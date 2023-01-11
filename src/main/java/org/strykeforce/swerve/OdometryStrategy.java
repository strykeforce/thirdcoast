package org.strykeforce.swerve;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;

/**
 * An {@code OdometryStrategy} provides an interface to one of several odometry calculation methods.
 */
public interface OdometryStrategy {

  /**
   * Returns the position of the robot on the field.
   *
   * @return the pose of the robot (x and y are in meters)
   */
  public Pose2d getPoseMeters();

  /**
   * Resets the robot's position on the field.
   *
   * @param pose the updated position
   * @param gyroAngle the current angle reported by the gyro
   * @param modulePositions the current rotation and positions of the swerve modules
   */
  public void resetPosition(
      Pose2d pose, Rotation2d gyroAngle, SwerveModulePosition... modulePositions);

  /**
   * Updates the robot's position on the field using forward kinematics and integration of the pose
   * over time.
   *
   * @param gyroAngle the current gyro angle
   * @param modulePositions the current rotation and positions of the swerve modules
   * @return the updated pose
   */
  public Pose2d update(Rotation2d gyroAngle, SwerveModulePosition... modulePositions);
}
