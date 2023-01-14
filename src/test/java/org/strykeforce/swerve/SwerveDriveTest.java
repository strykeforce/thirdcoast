package org.strykeforce.swerve;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.strykeforce.swerve.TestConstants.kMaxSpeedMetersPerSecond;
import static org.strykeforce.swerve.TestConstants.kWheelLocations;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.ArgumentCaptor;

class SwerveDriveTest {

  final ArgumentCaptor<SwerveModuleState> captor = ArgumentCaptor.forClass(SwerveModuleState.class);
  private final SwerveModule[] swerveModules = new SwerveModule[4];
  private Gyro gyro;

  @BeforeEach
  void setUp() {
    for (int i = 0; i < 4; i++) {
      swerveModules[i] = mock(SwerveModule.class);
      when(swerveModules[i].getWheelLocationMeters()).thenReturn(kWheelLocations[i]);
      when(swerveModules[i].getMaxSpeedMetersPerSecond()).thenReturn(kMaxSpeedMetersPerSecond);
      when(swerveModules[i].getPosition()).thenReturn(new SwerveModulePosition());
    }
    gyro = mock(Gyro.class);
  }

  @Test
  @DisplayName("Should throw on multiple max speeds")
  void shouldThrowOnMultipleMaxSpeeds() {

    for (int i = 0; i < 4; i++) {
      swerveModules[i] = mock(SwerveModule.class);
      when(swerveModules[i].getWheelLocationMeters()).thenReturn(kWheelLocations[i]);
      when(swerveModules[i].getMaxSpeedMetersPerSecond()).thenReturn(1.0 * i);
    }

    assertThrows(
        IllegalStateException.class,
        () -> {
          SwerveDrive swerveDrive = new SwerveDrive(swerveModules);
        });
  }

  @ParameterizedTest
  @CsvFileSource(resources = "/swerve_test_cases.csv", numLinesToSkip = 1)
  @DisplayName("Should produce correct swerve module states")
  void shouldProduceCorrectSwerveModuleStates(
      double vxMetersPerSecond,
      double vyMetersPerSecond,
      double omegaRadiansPerSecond,
      boolean isFieldOriented,
      double gyroAngle,
      double lfAngle,
      double lfSpeed,
      double rfAngle,
      double rfSpeed,
      double lrAngle,
      double lrSpeed,
      double rrAngle,
      double rrSpeed) {

    when(gyro.getRotation2d()).thenReturn(Rotation2d.fromDegrees(gyroAngle));
    SwerveDrive swerveDrive = new SwerveDrive(gyro, swerveModules);

    swerveDrive.drive(vxMetersPerSecond, vyMetersPerSecond, omegaRadiansPerSecond, isFieldOriented);
    verify(swerveModules[0]).setDesiredState(captor.capture(), eq(true));
    SwerveModuleState state = captor.getValue();

    assertEquals(lfAngle, state.angle.getDegrees(), 1e-9, "left front angle");
    assertEquals(lfSpeed, state.speedMetersPerSecond, 1e-9, "left front speed");

    verify(swerveModules[1]).setDesiredState(captor.capture(), eq(true));
    state = captor.getValue();

    assertEquals(rfAngle, state.angle.getDegrees(), 1e-9, "right front angle");
    assertEquals(rfSpeed, state.speedMetersPerSecond, 1e-9, "right front speed");

    verify(swerveModules[2]).setDesiredState(captor.capture(), eq(true));
    state = captor.getValue();

    assertEquals(lrAngle, state.angle.getDegrees(), 1e-9, "left rear angle");
    assertEquals(lrSpeed, state.speedMetersPerSecond, 1e-9, "left rear speed");

    verify(swerveModules[3]).setDesiredState(captor.capture(), eq(true));
    state = captor.getValue();

    assertEquals(rrAngle, state.angle.getDegrees(), 1e-9, "right rear angle");
    assertEquals(rrSpeed, state.speedMetersPerSecond, 1e-9, "right rear speed");
  }

  @Test
  void getKinematics() {
    when(gyro.getRotation2d()).thenReturn(Rotation2d.fromDegrees(-45));
    SwerveDrive swerveDrive = new SwerveDrive(gyro, swerveModules);
    assertNotNull(swerveDrive.getKinematics());
  }

  @Test
  void getPoseMeters() {
    when(gyro.getRotation2d()).thenReturn(Rotation2d.fromDegrees(-45));
    SwerveDrive swerveDrive = new SwerveDrive(gyro, swerveModules);
    assertNotNull(swerveDrive.getPoseMeters());
  }

  @Test
  void getHeading() {
    var expected = Rotation2d.fromDegrees(45);
    when(gyro.getRotation2d()).thenReturn(expected);
    SwerveDrive swerveDrive = new SwerveDrive(gyro, swerveModules);
    assertEquals(expected, swerveDrive.getHeading());
  }

  @Test
  void resetOdometry() {
    when(gyro.getRotation2d()).thenReturn(Rotation2d.fromDegrees(27));
    SwerveDrive swerveDrive = new SwerveDrive(gyro, swerveModules);
    Pose2d expected = new Pose2d(new Translation2d(2, 3), Rotation2d.fromDegrees(67));
    swerveDrive.resetOdometry(expected);
    assertEquals(expected, swerveDrive.getPoseMeters());
  }

  @Test
  void resetDriveEncoders() {
    when(gyro.getRotation2d()).thenReturn(Rotation2d.fromDegrees(27));
    SwerveDrive swerveDrive = new SwerveDrive(gyro, swerveModules);
    swerveDrive.resetDriveEncoders();
    for (int i = 0; i < 4; i++) {
      verify(swerveModules[i]).resetDriveEncoder();
    }
  }

  @Test
  void periodic() {
    when(gyro.getRotation2d()).thenReturn(Rotation2d.fromDegrees(27));
    SwerveDrive swerveDrive = new SwerveDrive(gyro, swerveModules);
    for (int i = 0; i < 4; i++) {
      when(swerveModules[i].getState())
          .thenReturn(new SwerveModuleState(1, Rotation2d.fromDegrees(1)));
    }
    swerveDrive.periodic();
    verify(gyro, times(2)).getRotation2d();
    for (int i = 0; i < 4; i++) {
      // verify(swerveModules[i]).getState();
      verify(swerveModules[i], times(2)).getPosition();
    }
  }

  @Test
  void setModuleStates() {
    when(gyro.getRotation2d()).thenReturn(Rotation2d.fromDegrees(27));
    SwerveDrive swerveDrive = new SwerveDrive(gyro, swerveModules);
    SwerveModuleState expectedState = new SwerveModuleState(1, Rotation2d.fromDegrees(3));
    SwerveModuleState[] desiredStates = new SwerveModuleState[4];
    for (int i = 0; i < 4; i++) {
      desiredStates[i] = expectedState;
    }
    swerveDrive.setModuleStates(desiredStates);
    for (int i = 0; i < 4; i++) {
      verify(swerveModules[i]).setDesiredState(expectedState, true);
    }
  }
}
