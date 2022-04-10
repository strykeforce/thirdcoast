package org.strykeforce.swerve;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;

/**
 * An optional odometry strategy that wraps {@code SwerveDrivePoseEstimator}, for use when vision
 * measurements are used to update odometry.
 */
public class PoseEstimatorOdometryStrategy implements OdometryStrategy {

  private final SwerveDrivePoseEstimator odometry;

  /**
   * Constructs a PoseEstimatorOdometryStrategy.
   *
   * @param gyroAngle The current gyro angle.
   * @param initialPoseMeters The starting pose estimate.
   * @param kinematics A correctly-configured kinematics object for your drivetrain.
   * @param stateStdDevs Standard deviations of model states. Increase these numbers to trust your
   *     model's state estimates less. This matrix is in the form [x, y, theta]ᵀ, with units in
   *     meters and radians.
   * @param localMeasurementStdDevs Standard deviations of the encoder and gyro measurements.
   *     Increase these numbers to trust sensor readings from encoders and gyros less. This matrix
   *     is in the form [theta], with units in radians.
   * @param visionMeasurementStdDevs Standard deviations of the vision measurements. Increase these
   *     numbers to trust global measurements from vision less. This matrix is in the form [x, y,
   *     theta]ᵀ, with units in meters and radians.
   * @param nominalDtSeconds The time in seconds between each robot loop.
   */
  PoseEstimatorOdometryStrategy(
      Rotation2d gyroAngle,
      Pose2d initialPoseMeters,
      SwerveDriveKinematics kinematics,
      Matrix<N3, N1> stateStdDevs,
      Matrix<N1, N1> localMeasurementStdDevs,
      Matrix<N3, N1> visionMeasurementStdDevs,
      double nominalDtSeconds) {
    odometry =
        new SwerveDrivePoseEstimator(
            gyroAngle,
            initialPoseMeters,
            kinematics,
            stateStdDevs,
            localMeasurementStdDevs,
            visionMeasurementStdDevs,
            nominalDtSeconds);
  }

  /**
   * Constructs a PoseEstimatorOdometryStrategy.
   *
   * @param gyroAngle The current gyro angle.
   * @param initialPoseMeters The starting pose estimate.
   * @param kinematics A correctly-configured kinematics object for your drivetrain.
   * @param stateStdDevs Standard deviations of model states. Increase these numbers to trust your
   *     model's state estimates less. This matrix is in the form [x, y, theta]ᵀ, with units in
   *     meters and radians.
   * @param localMeasurementStdDevs Standard deviations of the encoder and gyro measurements.
   *     Increase these numbers to trust sensor readings from encoders and gyros less. This matrix
   *     is in the form [theta], with units in radians.
   * @param visionMeasurementStdDevs Standard deviations of the vision measurements. Increase these
   *     numbers to trust global measurements from vision less. This matrix is in the form [x, y,
   *     theta]ᵀ, with units in meters and radians.
   */
  PoseEstimatorOdometryStrategy(
      Rotation2d gyroAngle,
      Pose2d initialPoseMeters,
      SwerveDriveKinematics kinematics,
      Matrix<N3, N1> stateStdDevs,
      Matrix<N1, N1> localMeasurementStdDevs,
      Matrix<N3, N1> visionMeasurementStdDevs) {
    this(
        gyroAngle,
        initialPoseMeters,
        kinematics,
        stateStdDevs,
        localMeasurementStdDevs,
        visionMeasurementStdDevs,
        0.02);
  }

  /**
   * Add a vision measurement to the Unscented Kalman Filter. This will correct the odometry pose
   * estimate while still accounting for measurement noise.
   *
   * <p>This method can be called as infrequently as you want, as long as you are calling {@link
   * SwerveDrivePoseEstimator#update} every loop.
   *
   * @param visionRobotPoseMeters The pose of the robot as measured by the vision camera.
   * @param timestampSeconds The timestamp of the vision measurement in seconds. Note that if you
   *     don't use your own time source by calling {@link SwerveDrivePoseEstimator#updateWithTime}
   *     then you must use a timestamp with an epoch since FPGA startup (i.e. the epoch of this
   *     timestamp is the same epoch as Timer.getFPGATimestamp.) This means that you should use
   *     Timer.getFPGATimestamp as your time source or sync the epochs.
   */
  void addVisionMeasurement(Pose2d visionRobotPoseMeters, double timestampSeconds) {
    odometry.addVisionMeasurement(visionRobotPoseMeters, timestampSeconds);
  }

  /**
   * Add a vision measurement to the Unscented Kalman Filter. This will correct the odometry pose
   * estimate while still accounting for measurement noise.
   *
   * <p>This method can be called as infrequently as you want, as long as you are calling {@link
   * SwerveDrivePoseEstimator#update} every loop.
   *
   * <p>Note that the vision measurement standard deviations passed into this method will continue
   * to apply to future measurements until a subsequent call to {@link
   * SwerveDrivePoseEstimator#setVisionMeasurementStdDevs(Matrix)} or this method.
   *
   * @param visionRobotPoseMeters The pose of the robot as measured by the vision camera.
   * @param timestampSeconds The timestamp of the vision measurement in seconds. Note that if you
   *     don't use your own time source by calling {@link SwerveDrivePoseEstimator#updateWithTime}
   *     then you must use a timestamp with an epoch since FPGA startup (i.e. the epoch of this
   *     timestamp is the same epoch as Timer.getFPGATimestamp.) This means that you should use
   *     Timer.getFPGATimestamp as your time source in this case.
   * @param visionMeasurementStdDevs Standard deviations of the vision measurements. Increase these
   *     numbers to trust global measurements from vision less. This matrix is in the form [x, y,
   *     theta]ᵀ, with units in meters and radians.
   */
  void addVisionMeasurement(
      Pose2d visionRobotPoseMeters,
      double timestampSeconds,
      Matrix<N3, N1> visionMeasurementStdDevs) {
    odometry.addVisionMeasurement(
        visionRobotPoseMeters, timestampSeconds, visionMeasurementStdDevs);
  }

  /**
   * Sets the pose estimator's trust of global measurements. This might be used to change trust in
   * vision measurements after the autonomous period, or to change trust as distance to a vision
   * target increases.
   *
   * @param visionMeasurementStdDevs Standard deviations of the vision measurements. Increase these
   *     numbers to trust global measurements from vision less. This matrix is in the form [x, y,
   *     theta]ᵀ, with units in meters and radians.
   */
  void setVisionMeasurementStdDevs(Matrix<N3, N1> visionMeasurementStdDevs) {
    odometry.setVisionMeasurementStdDevs(visionMeasurementStdDevs);
  }

  @Override
  public Pose2d getPoseMeters() {
    return odometry.getEstimatedPosition();
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
