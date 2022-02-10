package org.strykeforce.swerve;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import org.strykeforce.telemetry.Registrable;

/** Represents a Third Coast swerve module. */
public interface SwerveModule extends Registrable {

  /**
   * Gets the maximum attainable speed of the drive.
   *
   * @return max speed in meters/sec
   */
  double getMaxSpeedMetersPerSecond();

  /**
   * Provides the wheel location as Translation2d.
   *
   * @return wheel location in meters relative to center of robot
   */
  Translation2d getWheelLocationMeters();

  /**
   * Gets the current state of the swerve module.
   *
   * @return current state of the swerve module
   */
  SwerveModuleState getState();

  /**
   * Command the swerve module motors to the desired state.
   *
   * @param desiredState the desired swerve module speed and angle
   * @param isDriveOpenLoop true if drive should set speed using closed-loop velocity control
   */
  void setDesiredState(SwerveModuleState desiredState, boolean isDriveOpenLoop);

  /**
   * Command the swerve module motors to the desired state using closed-loop drive speed control.
   *
   * @param desiredState the desired swerve module speed and angle
   */
  default void setDesiredState(SwerveModuleState desiredState) {
    this.setDesiredState(desiredState, false);
  }

  /**
   * Command the swerve module azimuth rotation to the desired angle.
   *
   * @param rotation the desired absolute azimuth angle
   */
  void setAzimuthRotation2d(Rotation2d rotation);

  /**
   * Get the angle of the swerve drive azimuth.
   *
   * @return the angle of the azimuth rotation.
   */
  Rotation2d getAzimuthRotation2d();

  /** Resets the drive encoders to currently read a position of 0. */
  void resetDriveEncoder();

  /**
   * Save the current azimuth absolute encoder reference position in NetworkTables preferences. Call
   * this method following physical alignment of the module wheel in its zeroed position. Used
   * during module instantiation to initialize the relative encoder.
   */
  void storeAzimuthZeroReference();

  /**
   * Loads the current azimuth absolute encoder reference position and sets selected sensor encoder.
   */
  void loadAndSetAzimuthZeroReference();
}
