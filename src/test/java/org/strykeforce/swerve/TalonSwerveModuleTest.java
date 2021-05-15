package org.strykeforce.swerve;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.strykeforce.swerve.TestConstants.kDriveGearRatio;
import static org.strykeforce.swerve.TestConstants.kMaxSpeedMetersPerSecond;
import static org.strykeforce.swerve.TestConstants.kWheelDiameterInches;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.kinematics.SwerveModuleState;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;

class TalonSwerveModuleTest {

  static final NetworkTableInstance nti = NetworkTableInstance.create();

  @BeforeAll
  static void beforeAll() {
    nti.startLocal();
  }

  @AfterAll
  static void afterAll() {
    nti.stopLocal();
    nti.close();
  }

  @Test
  @DisplayName("Should set encoder counts per rev")
  void shouldSetEncoderCountsPerRev() {
    TalonSRX talonSRX = mock(TalonSRX.class);
    TalonFX talonFX = mock(TalonFX.class);
    TalonSwerveModule.Builder builder =
        new TalonSwerveModule.Builder()
            .azimuthTalon(talonSRX)
            .driveGearRatio(kDriveGearRatio)
            .wheelDiameterInches(kWheelDiameterInches)
            .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
            .wheelLocationMeters(new Translation2d());
    assertThat(builder.driveTalon(talonFX).build().getDriveCountsPerRev()).isEqualTo(2048);
    assertThat(builder.driveTalon(talonSRX).build().getDriveCountsPerRev()).isEqualTo(4096);
  }

  @Test
  @DisplayName("Should reset drive encoder")
  void resetDriveEncoder() {
    TalonSRX driveTalon = mock(TalonSRX.class);
    TalonSwerveModule module =
        new TalonSwerveModule.Builder()
            .azimuthTalon(mock(TalonSRX.class))
            .driveTalon(driveTalon)
            .driveGearRatio(kDriveGearRatio)
            .wheelDiameterInches(kWheelDiameterInches)
            .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
            .wheelLocationMeters(new Translation2d())
            .build();
    when(driveTalon.setSelectedSensorPosition(0)).thenReturn(ErrorCode.valueOf(0));
    module.resetDriveEncoder();
    verify(driveTalon).setSelectedSensorPosition(0);
  }

  @Nested
  @DisplayName("When setting azimuth zero")
  class WhenSettingAzimuthZero {

    TalonSRX azimuthTalon;
    TalonFX driveTalon;
    SwerveModule module;
    SensorCollection sensorCollection;

    @BeforeEach
    void setUp() {
      azimuthTalon = mock(TalonSRX.class);
      driveTalon = mock(TalonFX.class);
      module =
          new TalonSwerveModule.Builder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveGearRatio(kDriveGearRatio)
              .wheelDiameterInches(kWheelDiameterInches)
              .wheelLocationMeters(new Translation2d(1, 1))
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
              .build();
      sensorCollection = mock(SensorCollection.class);
      when(azimuthTalon.getSensorCollection()).thenReturn(sensorCollection);
    }

    @Test
    @DisplayName("should store azimuth zero reference")
    void storeAzimuthZeroReference() {
      TalonSwerveModule.Builder builder =
          new TalonSwerveModule.Builder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveGearRatio(kDriveGearRatio)
              .wheelDiameterInches(kWheelDiameterInches)
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond);

      Preferences preferences = Preferences.getInstance();

      int expectedZeroReference = 27;
      int index = 0; // fixture wheel is LF
      String key = String.format("SwerveDrive/wheel.%d", index);
      module = builder.wheelLocationMeters(new Translation2d(1, 1)).build();
      when(sensorCollection.getPulseWidthPosition()).thenReturn(expectedZeroReference);
      module.storeAzimuthZeroReference();
      assertEquals(expectedZeroReference, preferences.getInt(key, -1));

      expectedZeroReference = 67;
      index = 1; // fixture wheel is RF
      key = String.format("SwerveDrive/wheel.%d", index);
      module = builder.wheelLocationMeters(new Translation2d(1, -1)).build();
      when(sensorCollection.getPulseWidthPosition()).thenReturn(expectedZeroReference);
      module.storeAzimuthZeroReference();
      assertEquals(expectedZeroReference, preferences.getInt(key, -1));

      expectedZeroReference = 2767;
      index = 2; // fixture wheel is LR
      key = String.format("SwerveDrive/wheel.%d", index);
      module = builder.wheelLocationMeters(new Translation2d(-1, 1)).build();
      when(sensorCollection.getPulseWidthPosition()).thenReturn(expectedZeroReference);
      module.storeAzimuthZeroReference();
      assertEquals(expectedZeroReference, preferences.getInt(key, -1));

      expectedZeroReference = 6727;
      index = 3; // fixture wheel is RR
      key = String.format("SwerveDrive/wheel.%d", index);
      module = builder.wheelLocationMeters(new Translation2d(-1, -1)).build();
      when(sensorCollection.getPulseWidthPosition()).thenReturn(expectedZeroReference);
      module.storeAzimuthZeroReference();
      assertEquals(expectedZeroReference & 0xFFF, preferences.getInt(key, -1));
    }

    @ParameterizedTest
    @CsvSource({"0, 0, 0", "4096, 0, 0", "4097, 1, 0", "4097, 0, 1", "0, 2767, -2767"})
    @DisplayName("should set azimuth zero")
    void shouldSetAzimuthZero(int absoluteEncoderPosition, int zeroReference, double setpoint) {
      int index = 0; // fixture wheel is LF
      String key = String.format("SwerveDrive/wheel.%d", index);
      Preferences preferences = Preferences.getInstance();
      preferences.putInt(key, zeroReference);
      when(sensorCollection.getPulseWidthPosition()).thenReturn(absoluteEncoderPosition);
      when(azimuthTalon.setSelectedSensorPosition(eq(setpoint), anyInt(), anyInt()))
          .thenReturn(ErrorCode.valueOf(0));
      module.loadAndSetAzimuthZeroReference();
      verify(azimuthTalon).setSelectedSensorPosition(setpoint, 0, 10);
    }

    @Test
    @DisplayName("should throw exception if no NetworkTables reference")
    void shouldThrowExceptionIfNoNetworkTablesReference() {
      int index = 0; // fixture wheel is LF
      String key = String.format("SwerveDrive/wheel.%d", index);
      Preferences preferences = Preferences.getInstance();
      preferences.remove(key);
      int reference = preferences.getInt(key, Integer.MIN_VALUE);
      assertEquals(Integer.MIN_VALUE, reference);

      when(sensorCollection.getPulseWidthPosition()).thenReturn(0);
      when(azimuthTalon.setSelectedSensorPosition(anyDouble(), anyInt(), anyInt()))
          .thenReturn(ErrorCode.valueOf(0));
      assertThrows(IllegalStateException.class, module::loadAndSetAzimuthZeroReference);
    }
  }

  /*
    @Test
    @DisplayName("Rotation2d should work as expected")
    void rotation2DShouldWorkAsExpected() {
      assertEquals(Rotation2d.fromDegrees(180), Rotation2d.fromDegrees(-180));

      var a = Rotation2d.fromDegrees(175);
      var b = Rotation2d.fromDegrees(10);

      assertEquals(Rotation2d.fromDegrees(165), a.minus(b));
      assertEquals(Rotation2d.fromDegrees(185), a.plus(b));
      assertEquals(Rotation2d.fromDegrees(-175), a.plus(b));
      assertEquals(-175.0, a.plus(b).getDegrees());

      var c = new Rotation2d(Math.PI);
      assertEquals(Rotation2d.fromDegrees(-175), a.rotateBy(b));
      assertEquals(Rotation2d.fromDegrees(-5), a.rotateBy(c));

      assertEquals(Rotation2d.fromDegrees(-175), a.unaryMinus());

      var d = Rotation2d.fromDegrees(360);
      assertEquals(Rotation2d.fromDegrees(1), d.plus(Rotation2d.fromDegrees(1)));

      var revolutions = 3;
      var e = new Rotation2d(2.0 * Math.PI * revolutions);
      assertEquals(new Rotation2d(), e);
      assertEquals(1080, e.getDegrees());
      assertEquals(0, e.rotateBy(new Rotation2d()).getRadians(), 1e-9);
    }
  */

  @Nested
  @DisplayName("Should not validate")
  class ShouldNotValidate {

    private TalonSRX azimuthTalon;
    private TalonFX driveTalon;

    @BeforeEach
    void setUp() {
      azimuthTalon = mock(TalonSRX.class);
      driveTalon = mock(TalonFX.class);
    }

    @Test
    @DisplayName("when talon is null")
    void whenTalonIsNull() {
      var builder =
          new TalonSwerveModule.Builder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveGearRatio(kDriveGearRatio)
              .wheelDiameterInches(kWheelDiameterInches)
              .wheelLocationMeters(new Translation2d(1, 1))
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond);

      assertThrows(IllegalArgumentException.class, builder.azimuthTalon(null)::build);
      assertThrows(IllegalArgumentException.class, () -> builder.driveTalon(null));
    }

    @Test
    @DisplayName("when drive gear ratio lte zero")
    void whenDriveGearRatioLteZero() {
      TalonSwerveModule.Builder builder =
          new TalonSwerveModule.Builder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .wheelDiameterInches(kWheelDiameterInches)
              .wheelLocationMeters(new Translation2d())
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond);
      assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    @DisplayName("when wheel diameter lte zero")
    void whenWheelDiameterLteZero() {
      TalonSwerveModule.Builder builder =
          new TalonSwerveModule.Builder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveGearRatio(kDriveGearRatio)
              .wheelLocationMeters(new Translation2d())
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond);
      assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    @DisplayName("when drive maximum meters per second lte zero")
    void whenDriveMaximumMetersPerSecondLteZero() {
      TalonSwerveModule.Builder builder =
          new TalonSwerveModule.Builder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveGearRatio(kDriveGearRatio)
              .wheelLocationMeters(new Translation2d())
              .wheelDiameterInches(kWheelDiameterInches);
      assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    @DisplayName("when wheel location not set")
    void whenWheelLocationNotSet() {
      TalonSwerveModule.Builder builder =
          new TalonSwerveModule.Builder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveGearRatio(kDriveGearRatio)
              .wheelDiameterInches(kWheelDiameterInches)
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond);
      assertThrows(IllegalArgumentException.class, builder::build);
    }
  }

  @Nested
  @DisplayName("Should get module state")
  class ShouldGetModuleState {

    private TalonSRX azimuthTalon;
    private TalonFX driveTalon;

    @BeforeEach
    void setUp() {
      azimuthTalon = mock(TalonSRX.class);
      driveTalon = mock(TalonFX.class);
    }

    @Test
    @DisplayName("drive speed with default encoder counts")
    void speedWithDefaultEncoderCounts() {
      TalonSwerveModule module =
          new TalonSwerveModule.Builder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveGearRatio(kDriveGearRatio)
              .wheelDiameterInches(kWheelDiameterInches)
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
              .wheelLocationMeters(new Translation2d())
              .build();
      when(driveTalon.getSelectedSensorVelocity()).thenReturn(20480.0);
      SwerveModuleState state = module.getState();
      assertEquals(3.657337448, state.speedMetersPerSecond, 1e-9);
    }

    @ParameterizedTest
    @CsvSource({"2048, 20480, 3.657337448", "4096, 40960, 3.657337448"})
    @DisplayName("drive speed with encoder counts")
    void speedWithEncoderCounts(
        int driveEncoderCountsPerRevolution,
        double driveSelectedSensorVelocity,
        double expectedMetersPerSecond) {
      TalonSwerveModule module =
          new TalonSwerveModule.Builder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              //              .azimuthEncoderCountsPerRevolution(4096)
              .driveEncoderCountsPerRevolution(driveEncoderCountsPerRevolution)
              .driveGearRatio(kDriveGearRatio)
              .wheelDiameterInches(kWheelDiameterInches)
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
              .wheelLocationMeters(new Translation2d())
              .build();
      when(driveTalon.getSelectedSensorVelocity()).thenReturn(driveSelectedSensorVelocity);
      SwerveModuleState state = module.getState();
      assertEquals(expectedMetersPerSecond, state.speedMetersPerSecond, 1e-9);
    }

    @ParameterizedTest
    @CsvSource({
      // azimuth counts per rev, azimuth encoder counts, azimuth angle
      // cardinals
      "4096, 0, 0",
      "4096, 1024, 90",
      "2048, 512, 90",
      "4096, 2048, 180",
      "2048, 1024, 180",
      "4096, 3072, -90",
      "2048, 1536, -90",
      // cardinals plus 4096/2048
      "4096, 4096, 0",
      "2048, 2048, 0",
      "4096, 5120, 90",
      "2048, 2560, 90",
      "4096, 6144, 180",
      "2048, 3072, 180",
      "4096, 7168, -90",
      // negatives
      "4096, -0, 0",
      "2048, -0, 0",
      "4096, -1024, -90",
      "2048, -512, -90",
      "4096, -2048, -180",
      "2048, -1024, -180",
      "4096, -3072, 90",
      "2048, -1536, 90",
      // negatives minus 4096/2048
      "4096, -4096, 0",
      "2048, -2048, 0",
      "4096, -5120, -90",
      "2048, -2560, -90",
      "4096, -6144, -180",
      "2048, -3072, -180",
      "4096, -7168, 90",
      "2048, -3584, 90",
      // misc
      "4096, 11.377778, 1", // 4096/360
      "4096, -11.377778, -1"
    })
    @DisplayName("azimuth angle with encoder counts")
    void angleWithEncoderCounts(
        int azimuthEncoderCountsPerRevolution,
        double azimuthSelectedSensorPosition,
        double expectedAngleDeg) {
      // we currently only support TalonSRX for azimuth
      if (azimuthEncoderCountsPerRevolution == 2048) {
        return;
      }

      TalonSwerveModule module =
          new TalonSwerveModule.Builder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              //              .azimuthEncoderCountsPerRevolution(azimuthEncoderCountsPerRevolution)
              .driveGearRatio(kDriveGearRatio)
              .wheelDiameterInches(kWheelDiameterInches)
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
              .wheelLocationMeters(new Translation2d())
              .build();
      when(azimuthTalon.getSelectedSensorPosition()).thenReturn(azimuthSelectedSensorPosition);
      SwerveModuleState state = module.getState();
      var expectedRotation = Rotation2d.fromDegrees(expectedAngleDeg);
      assertEquals(expectedRotation, state.angle);
    }

    @Test
    void getMaxSpeedMetersPerSecond() {
      TalonSwerveModule module =
          new TalonSwerveModule.Builder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveGearRatio(kDriveGearRatio)
              .wheelDiameterInches(kWheelDiameterInches)
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
              .wheelLocationMeters(new Translation2d())
              .build();
      assertEquals(kMaxSpeedMetersPerSecond, module.getMaxSpeedMetersPerSecond());
    }

    @Test
    void getWheelLocationMeters() {
      var expectedWheelLocation = new Translation2d(27, 67);
      TalonSwerveModule module =
          new TalonSwerveModule.Builder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveGearRatio(kDriveGearRatio)
              .wheelDiameterInches(kWheelDiameterInches)
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
              .wheelLocationMeters(expectedWheelLocation)
              .build();
      assertEquals(expectedWheelLocation, module.getWheelLocationMeters());
    }
  }

  @Nested
  @DisplayName("Should set module state")
  class ShouldSetModuleState {

    final ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
    private TalonSRX azimuthTalon;
    private TalonFX driveTalon;

    @BeforeEach
    void setUp() {
      azimuthTalon = mock(TalonSRX.class);
      driveTalon = mock(TalonFX.class);
    }

    @ParameterizedTest
    @CsvSource({
      // drive counts per rev, drive m/s, percent output
      "2048, 0, 0",
      "2048, 0.304778121, 0.079365079",
      "2048, 3.657337448, 0.9523809525",
      "4096, 0, 0",
      "4096, 0.304778121, 0.079365079",
      "4096, 3.657337448, 0.9523809525",
    })
    @DisplayName("drive speed when open loop")
    void driveOpenLoopSpeed(
        int driveEncoderCountsPerRevolution,
        double driveMetersPerSecond,
        double expectedPercentOutput) {
      TalonSwerveModule module =
          new TalonSwerveModule.Builder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveEncoderCountsPerRevolution(driveEncoderCountsPerRevolution)
              .driveGearRatio(kDriveGearRatio)
              .wheelDiameterInches(kWheelDiameterInches)
              .wheelLocationMeters(new Translation2d())
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
              .build();
      when(azimuthTalon.getSelectedSensorPosition()).thenReturn(0.0);
      var desiredState = new SwerveModuleState(driveMetersPerSecond, new Rotation2d());
      module.setDesiredState(desiredState, true);
      verify(driveTalon).set(eq(ControlMode.PercentOutput), captor.capture());
      assertEquals(expectedPercentOutput, captor.getValue(), 1e-9);
    }

    @ParameterizedTest
    @CsvSource({
      // drive counts per rev, drive m/s, encoder counts per 100ms
      "2048, 0, 0",
      "2048, 0.304778121, 1707",
      "2048, 0.609556241, 3413",
      "2048, 1.219112483, 6827",
      "2048, 3.84020432, 21504",
      "4096, 0, 0",
      "4096, 0.304778121, 3414",
      "4096, 0.609556241, 6826",
      "4096, 1.219112483, 13654",
      "4096, 3.84020432, 43008",
    })
    @DisplayName("drive speed when closed loop")
    void driveClosedLoopSpeed(
        int driveEncoderCountsPerRevolution,
        double driveMetersPerSecond,
        double expectedCountsPer100ms) {
      TalonSwerveModule module =
          new TalonSwerveModule.Builder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveEncoderCountsPerRevolution(driveEncoderCountsPerRevolution)
              .driveGearRatio(kDriveGearRatio)
              .wheelDiameterInches(kWheelDiameterInches)
              .wheelLocationMeters(new Translation2d())
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
              .build();

      when(azimuthTalon.getSelectedSensorPosition()).thenReturn(0.0);
      var desiredState = new SwerveModuleState(driveMetersPerSecond, new Rotation2d());
      module.setDesiredState(desiredState, false);
      verify(driveTalon).set(eq(ControlMode.Velocity), captor.capture());
      assertEquals(expectedCountsPer100ms, captor.getValue(), 0.75);
    }

    @ParameterizedTest
    @CsvSource({
      // drive counts per rev, encoder counts before, azimuth angle, encoder counts after, drive
      // reversed
      "4096, 0, 0, 0, false",
      "4096, 0, 10, 114, false",
      "4096, 0, -10, -114, false",
      "4096, 0, 90, 1024, false",
      "4096, 0, -90, -1024, false",
      "4096, 0, 135, -512, true",
      "4096, 0, -135, 512, true",
      "4096, 1024, 180, 2048, false",
      "4096, 1024, -180, 0, true",
      "4096, -1024, -180, -2048, false",
      "4096, -1024, 180, 0, true",
      "4096, 2048, 180, 2048, false",
      "4096, 2048, -180, 2048, false",
      "4096, -2048, -180, -2048, false",
      "4096, -2048, 180, -2048, false",
      "2048, 0, 0, 0, false",
      "2048, 0, 10, 57, false",
      "2048, 0, -10, -57, false",
      "2048, 0, 90, 512, false",
      "2048, 0, -90, -512, false",
      "2048, 0, 135, -256, true",
      "2048, 0, -135, 256, true",
      "2048, 1024, 180, 1024, false",
      "2048, 1024, -180, 1024, false",
      "2048, -1024, -180, -1024, false",
      "2048, -1024, 180, -1024, false",
      "2048, 2048, 180, 2048, true",
      "2048, 2048, -180, 2048, true",
      "2048, -2048, -180, -2048, true",
      "2048, -2048, 180, -2048, true",
      // encoder wound up
      // 12288=0 (3 revs), 13312=90, 11264=-90
      "4096, 12288, 0, 12288, false",
      "4096, 12289, 90, 13312, false",
      "4096, 12287, 90, 11264, true",
      "4096, 12288, 90, 11264, true",
      "2048, 12288, 0, 12288, false",
      "2048, 12289, 90, 12800, false",
      "2048, 12287, 90, 11776, true",
      "2048, 12288, 90, 11776, true",
      // 13369=95, 14336=180, 14393=185, 12356=6
      "4096, 13369, 95, 13369, false",
      "4096, 14336, 180, 14336, false",
      "4096, 14336, -180, 14336, false",
      "4096, 13369, 180, 14336, false",
      "4096, 13369, 185, 14393, false",
      "4096, 13369, 186, 12356, true",
      "2048, 13369, 95, 13852, true",
      "2048, 14336, 180, 14336, true",
      "2048, 14336, -180, 14336, true",
      "2048, 13369, 180, 13312, false",
      "2048, 13369, 185, 13340, false",
      "2048, 13369, 186, 13346, false",
    })
    @DisplayName("azimuth angle  when open loop")
    void azimuthAngleWhenOpenLoop(
        int azimuthEncoderCountsPerRevolution,
        double countsBefore,
        double angleDegrees,
        double countsExpected,
        boolean reversed) {
      // we currently only support TalonSRX for azimuth
      if (azimuthEncoderCountsPerRevolution == 2048) {
        return;
      }

      var speedMetersPerSecond = 3.657337448;
      var drivePercentOutput = 0.9523809525;

      TalonSwerveModule module =
          new TalonSwerveModule.Builder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              //              .azimuthEncoderCountsPerRevolution(azimuthEncoderCountsPerRevolution)
              .driveGearRatio(kDriveGearRatio)
              .wheelDiameterInches(kWheelDiameterInches)
              .wheelLocationMeters(new Translation2d())
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
              .build();

      var desiredState =
          new SwerveModuleState(speedMetersPerSecond, Rotation2d.fromDegrees(angleDegrees));

      when(azimuthTalon.getSelectedSensorPosition()).thenReturn(countsBefore);

      module.setDesiredState(desiredState, true);
      verify(azimuthTalon).set(eq(ControlMode.MotionMagic), captor.capture());
      assertEquals(countsExpected, captor.getValue(), 0.5);
      verify(driveTalon).set(eq(ControlMode.PercentOutput), captor.capture());
      assertEquals(reversed ? -drivePercentOutput : drivePercentOutput, captor.getValue(), 1e-6);
    }
  }
}
