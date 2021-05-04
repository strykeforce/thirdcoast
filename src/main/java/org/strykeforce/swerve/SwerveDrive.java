package org.strykeforce.swerve;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.SwerveDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.SwerveDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.SwerveModuleState;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Control a Third Coast swerve drive.
 */
public class SwerveDrive {

  private static final Logger logger = LoggerFactory.getLogger(SwerveDrive.class);

  private final SwerveModule[] swerveModules;
  private final SwerveDriveKinematics kinematics;
  private final SwerveDriveOdometry odometry;
  private final Gyro gyro;
  private final double maxSpeedMetersPerSecond;

  /**
   * Construct a swerve drive object. Along with a gyro, this takes in four configured swerve
   * modules, by convention in left front, right front, left rear, right rear order.
   *
   * @param gyro          the gyro to use for field-oriented driving
   * @param swerveModules the swerve modules
   */
  public SwerveDrive(Gyro gyro, SwerveModule... swerveModules) {
    this.gyro = gyro;
    this.swerveModules = swerveModules;
    final List<Translation2d> locations = Arrays.stream(swerveModules)
        .map(SwerveModule::getWheelLocationMeters)
        .collect(Collectors.toList());

    Translation2d[] translation2ds = Arrays.stream(swerveModules)
        .map(SwerveModule::getWheelLocationMeters)
        .toArray(Translation2d[]::new);

    // verify all swerve modules are set to same max speed
    Set<Double> maxSpeeds = Arrays.stream(swerveModules)
        .map(SwerveModule::getMaxSpeedMetersPerSecond)
        .collect(Collectors.toSet());

    if (maxSpeeds.size() > 1) {
      throw new IllegalStateException("swerve modules must have same driveMaximumMetersPerSecond");
    }
    maxSpeedMetersPerSecond = swerveModules[0].getMaxSpeedMetersPerSecond();

    kinematics = new SwerveDriveKinematics(translation2ds);
    odometry = new SwerveDriveOdometry(kinematics, gyro.getRotation2d());
  }

  /**
   * Construct a swerve drive object with a navX gyro. This takes in four configured swerve modules,
   * by convention in left front, right front, left rear, right rear order.
   *
   * @param swerveModules the swerve modules
   */
  public SwerveDrive(SwerveModule... swerveModules) {
    this(new AHRS(), swerveModules);
  }

  /**
   * Returns the kinematics object in use by this swerve drive.
   *
   * @return The kinematics object in use.
   */
  public SwerveDriveKinematics getKinematics() {
    return kinematics;
  }

  /**
   * Returns the position of the robot on the field.
   *
   * @return the pose of the robot (x and y ane in meters)
   */
  public Pose2d getPoseMeters() {
    return odometry.getPoseMeters();
  }

  /**
   * Returns the current gyro-measured heading of the robot. This will be affected by any gyro drift
   * that may have accumulated since last gyro recalibration.
   *
   * @return the angle of the robot relative to gyro zero
   */
  public Rotation2d getHeading() {
    return gyro.getRotation2d();
  }

  /**
   * Resets the robot's position on the field. Any accumulated gyro drift will be noted and
   * accounted for in subsequent calls to {@link #getPoseMeters()}.
   *
   * @param pose The robot's actual position on the field.
   */
  public void resetOdometry(Pose2d pose) {
    odometry.resetPosition(pose, gyro.getRotation2d());
  }

  /**
   * Resets the drive encoders to currently read a position of 0.
   */
  public void resetDriveEncoders() {
    for (int i = 0; i < 4; i++) {
      swerveModules[i].resetDriveEncoder();
    }
  }

  /**
   * Resets the gyro to a heading of zero.
   */
  public void resetGyro() {
    gyro.reset();
  }

  /**
   * Update the swerve drive odometry state. Call this from the drive subsystem {@code periodic()}
   * method.
   */
  public void periodic() {
    odometry.update(gyro.getRotation2d(), swerveModules[0].getState(), swerveModules[1].getState(),
        swerveModules[2].getState(), swerveModules[3].getState());
  }

  /**
   * Drive the robot with given x, y, and rotational velocities using open-loop velocity control.
   *
   * @param vxMetersPerSecond     the desired x velocity component
   * @param vyMetersPerSecond     the desired y velocity component
   * @param omegaRadiansPerSecond the desired rotational velocity component
   * @param isFieldOriented       true if driving field-oriented
   */
  public void drive(double vxMetersPerSecond, double vyMetersPerSecond,
      double omegaRadiansPerSecond, boolean isFieldOriented) {
    SwerveModuleState[] swerveModuleStates = getSwerveModuleStates(
        vxMetersPerSecond, vyMetersPerSecond, omegaRadiansPerSecond, isFieldOriented);
    for (int i = 0; i < 4; i++) {
      swerveModules[i].setDesiredState(swerveModuleStates[i], true);
    }
  }

  /**
   * Move the robot with given x, y, and rotational velocities using closed-loop velocity control.
   *
   * @param vxMetersPerSecond     the desired x velocity component
   * @param vyMetersPerSecond     the desired y velocity component
   * @param omegaRadiansPerSecond the desired rotational velocity component
   * @param isFieldOriented       true if driving field-oriented
   */
  public void move(double vxMetersPerSecond, double vyMetersPerSecond,
      double omegaRadiansPerSecond, boolean isFieldOriented) {
    SwerveModuleState[] swerveModuleStates = getSwerveModuleStates(
        vxMetersPerSecond, vyMetersPerSecond, omegaRadiansPerSecond, isFieldOriented);
    for (int i = 0; i < 4; i++) {
      swerveModules[i].setDesiredState(swerveModuleStates[i], false);
    }
  }

  @NotNull
  private SwerveModuleState[] getSwerveModuleStates(double vxMetersPerSecond,
      double vyMetersPerSecond, double omegaRadiansPerSecond, boolean isFieldOriented) {
    ChassisSpeeds chassisSpeeds = isFieldOriented ? ChassisSpeeds
        .fromFieldRelativeSpeeds(vxMetersPerSecond, vyMetersPerSecond, omegaRadiansPerSecond,
            gyro.getRotation2d())
        : new ChassisSpeeds(vxMetersPerSecond, vyMetersPerSecond, omegaRadiansPerSecond);

    var swerveModuleStates = kinematics.toSwerveModuleStates(chassisSpeeds);
    SwerveDriveKinematics.normalizeWheelSpeeds(swerveModuleStates, maxSpeedMetersPerSecond);
    return swerveModuleStates;
  }

  /**
   * Directly set the swerve modules to the specified states.
   *
   * @param desiredStates the desired swerve module states
   */
  public void setModuleStates(SwerveModuleState[] desiredStates) {
    SwerveDriveKinematics.normalizeWheelSpeeds(desiredStates, maxSpeedMetersPerSecond);
    for (int i = 0; i < 4; i++) {
      swerveModules[i].setDesiredState(desiredStates[i], true);
    }
  }

}
