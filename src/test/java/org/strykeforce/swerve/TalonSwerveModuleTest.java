package org.strykeforce.swerve;

import static edu.wpi.first.units.Units.Rotations;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.strykeforce.swerve.TestConstants.kDriveGearRatio;
import static org.strykeforce.swerve.TestConstants.kMaxSpeedMetersPerSecond;
import static org.strykeforce.swerve.TestConstants.kWheelDiameterInches;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.Timestamp;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.MotionMagicDutyCycle;
import com.ctre.phoenix6.controls.VelocityDutyCycle;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.hardware.TalonFXS;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj.Preferences;
import java.lang.reflect.Field;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.platform.commons.util.ReflectionUtils;
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
  @DisplayName("V6 Should set encoder counts per rev")
  void v6ShouldSetEncoderCountsPerREv() {
    TalonSRX talonSRX = mock(TalonSRX.class);
    TalonFX talonFx = mock(com.ctre.phoenix6.hardware.TalonFX.class);
    when(talonSRX.setSelectedSensorPosition(0)).thenReturn(ErrorCode.valueOf(0));
    when(talonFx.setPosition(0)).thenReturn(StatusCode.OK);
    V6TalonSwerveModule.V6Builder builder =
        new V6TalonSwerveModule.V6Builder()
            .azimuthTalon(talonSRX)
            .driveTalon(talonFx)
            .driveGearRatio(kDriveGearRatio)
            .wheelDiameterInches(kWheelDiameterInches)
            .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
            .wheelLocationMeters(new Translation2d());
    assertThat(builder.build().getDriveCountsPerRev()).isEqualTo(1);
    assertThat(builder.driveEncoderCountsPerRevolution(2).build().getDriveCountsPerRev())
        .isEqualTo(2);
  }

  @Test
  @DisplayName("FX Should set encoder counts per rev")
  void FXShouldSetEncoderCountsPerREv() {
    TalonFXS talonFXS = mock(TalonFXS.class);
    TalonFX talonFx = mock(com.ctre.phoenix6.hardware.TalonFX.class);
    when(talonFXS.setPosition(0)).thenReturn(StatusCode.OK);
    when(talonFx.setPosition(0)).thenReturn(StatusCode.OK);
    FXSwerveModule.FXBuilder builder =
        new FXSwerveModule.FXBuilder()
            .azimuthTalon(talonFXS)
            .driveTalon(talonFx)
            .driveGearRatio(kDriveGearRatio)
            .wheelDiameterInches(kWheelDiameterInches)
            .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
            .wheelLocationMeters(new Translation2d())
            .encoderOpposed(false);
    assertThat(builder.build().getDriveCountsPerRev()).isEqualTo(1);
    assertThat(builder.driveEncoderCountsPerRevolution(2).build().getDriveCountsPerRev())
        .isEqualTo(2);
  }

  @Test
  @DisplayName("V6 Should reset drive encoder")
  void v6ResetDriveEncoder() {
    TalonFX driveTalon = mock(com.ctre.phoenix6.hardware.TalonFX.class);
    when(driveTalon.setPosition(0)).thenReturn(StatusCode.OK);
    V6TalonSwerveModule module =
        new V6TalonSwerveModule.V6Builder()
            .azimuthTalon(mock(TalonSRX.class))
            .driveTalon(driveTalon)
            .driveGearRatio(kDriveGearRatio)
            .wheelDiameterInches(kWheelDiameterInches)
            .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
            .wheelLocationMeters(new Translation2d())
            .build();
    module.resetDriveEncoder();
    verify(driveTalon, times(2)).setPosition(0);
  }

  @Test
  @DisplayName("FX Should reset drive encoder")
  void FXResetDriveEncoder() {
    TalonFX driveTalon = mock(com.ctre.phoenix6.hardware.TalonFX.class);
    TalonFXS azimuthTalon = mock(TalonFXS.class);
    when(driveTalon.setPosition(0)).thenReturn(StatusCode.OK);
    FXSwerveModule module =
        new FXSwerveModule.FXBuilder()
            .azimuthTalon(azimuthTalon)
            .driveTalon(driveTalon)
            .driveGearRatio(kDriveGearRatio)
            .wheelDiameterInches(kWheelDiameterInches)
            .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
            .wheelLocationMeters(new Translation2d())
            .encoderOpposed(false)
            .build();
    module.resetDriveEncoder();
    verify(driveTalon, times(2)).setPosition(0);
  }

  @Nested
  @DisplayName("When setting V6 azimuth zero")
  class TestWhenSettingV6AzimuthZero {
    TalonSRX azimuthTalon;
    TalonFX driveTalon;
    V6TalonSwerveModule module;
    SensorCollection sensorCollection;

    @BeforeEach
    void setUp() {
      azimuthTalon = mock(TalonSRX.class);
      driveTalon = mock(TalonFX.class);
      when(driveTalon.setPosition(0)).thenReturn(StatusCode.OK);
      module =
          new V6TalonSwerveModule.V6Builder()
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
    @DisplayName("should store V6 aziumth zero reference")
    void storeV6AzimuthZeroReference() {
      V6TalonSwerveModule.V6Builder builder =
          new V6TalonSwerveModule.V6Builder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveGearRatio(kDriveGearRatio)
              .wheelDiameterInches(kWheelDiameterInches)
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond);

      int expectedZeroReference = 27;
      int index = 0; // Front Left
      String key = String.format("SwerveDrive/wheel.%d", index);
      module = builder.wheelLocationMeters(new Translation2d(1, 1)).build();
      when(sensorCollection.getPulseWidthPosition()).thenReturn(expectedZeroReference);
      module.storeAzimuthZeroReference();
      assertEquals(expectedZeroReference, Preferences.getInt(key, -1));

      expectedZeroReference = 67;
      index = 1; // Front Right
      key = String.format("SwerveDrive/wheel.%d", index);
      module = builder.wheelLocationMeters(new Translation2d(1, -1)).build();
      when(sensorCollection.getPulseWidthPosition()).thenReturn(expectedZeroReference);
      module.storeAzimuthZeroReference();
      assertEquals(expectedZeroReference, Preferences.getInt(key, -1));

      expectedZeroReference = 2767;
      index = 2; // Back Left
      key = String.format("SwerveDrive/wheel.%d", index);
      module = builder.wheelLocationMeters(new Translation2d(-1, 1)).build();
      when(sensorCollection.getPulseWidthPosition()).thenReturn(expectedZeroReference);
      module.storeAzimuthZeroReference();
      assertEquals(expectedZeroReference, Preferences.getInt(key, -1));

      expectedZeroReference = 6727;
      index = 3; // Back Right
      key = String.format("SwerveDrive/wheel.%d", index);
      module = builder.wheelLocationMeters(new Translation2d(-1, -1)).build();
      when(sensorCollection.getPulseWidthPosition()).thenReturn(expectedZeroReference);
      module.storeAzimuthZeroReference();
      assertEquals(expectedZeroReference & 0xFFF, Preferences.getInt(key, -1));
    }

    @ParameterizedTest
    @CsvSource({"0, 0, 0", "4096, 0, 0", "4097, 0, 1", "0, 2767, -2767"})
    @DisplayName("should set V6 azimuth zero")
    void shouldSetV6AzimuthZero(int absoluteEncoderPosition, int zeroReference, double setpoint) {
      int index = 0; // Front Left
      String key = String.format("SwerveDrive/wheel.%d", index);
      Preferences.setInt(key, zeroReference);
      when(sensorCollection.getPulseWidthPosition()).thenReturn(absoluteEncoderPosition);
      when(azimuthTalon.setSelectedSensorPosition(eq(setpoint), anyInt(), anyInt()))
          .thenReturn(ErrorCode.valueOf(0));
      module.loadAndSetAzimuthZeroReference();
      verify(azimuthTalon).setSelectedSensorPosition(setpoint, 0, 10);
    }
  }

  @Nested
  @DisplayName("When setting FX azimuth zero")
  class TestWhenSettingFXAzimuthZero {
    TalonFXS azimuthTalon;
    TalonFX driveTalon;
    FXSwerveModule module;

    StatusSignal<Angle> azimuthPositionStatusSig;
    StatusSignal<Angle> rawPulseWidthStatusSig;
    final ArgumentCaptor<MotionMagicDutyCycle> posDutyCycleCaptor =
        ArgumentCaptor.forClass(MotionMagicDutyCycle.class);

    @BeforeEach
    void setUp() {
      azimuthTalon = mock(TalonFXS.class);
      driveTalon = mock(TalonFX.class);
      when(driveTalon.setPosition(0)).thenReturn(StatusCode.OK);
      module =
          new FXSwerveModule.FXBuilder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveGearRatio(kDriveGearRatio)
              .wheelDiameterInches(kWheelDiameterInches)
              .wheelLocationMeters(new Translation2d(1, 1))
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
              .encoderOpposed(false)
              .build();
      azimuthPositionStatusSig = (StatusSignal<Angle>) mock(StatusSignal.class);
      rawPulseWidthStatusSig = (StatusSignal<Angle>) mock(StatusSignal.class);
      Field azimuthPosField =
          ReflectionUtils.findFields(
                  FXSwerveModule.class,
                  f -> f.getName().equals("azimuthPosition"),
                  ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
              .get(0);
      azimuthPosField.setAccessible(true);
      try {
        azimuthPosField.set(module, azimuthPositionStatusSig);
      } catch (Exception e) {
        throw new IllegalArgumentException("Error: " + e);
      }
      when(azimuthTalon.getPosition()).thenReturn(azimuthPositionStatusSig);
      when(azimuthPositionStatusSig.getValueAsDouble()).thenReturn(0.0);
    }

    @Test
    @DisplayName("should store FX aziumth zero reference")
    void storeFXAzimuthZeroReference() {
      FXSwerveModule.FXBuilder builder =
          new FXSwerveModule.FXBuilder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveGearRatio(kDriveGearRatio)
              .wheelDiameterInches(kWheelDiameterInches)
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
              .encoderOpposed(false);

      double expectedZeroReference = 27.0 / 4096.0;
      int index = 0; // Front Left
      String key = String.format("SwerveDrive/wheel.%d", index);
      module = builder.wheelLocationMeters(new Translation2d(1, 1)).build();
      when(azimuthTalon.getPosition()).thenReturn(azimuthPositionStatusSig);
      when(azimuthPositionStatusSig.getValueAsDouble()).thenReturn(expectedZeroReference);
      when(azimuthTalon.getRawPulseWidthPosition()).thenReturn(rawPulseWidthStatusSig);
      when(rawPulseWidthStatusSig.getValueAsDouble()).thenReturn(expectedZeroReference);
      module.storeAzimuthZeroReference();
      assertEquals(expectedZeroReference, Preferences.getDouble(key, -1));

      expectedZeroReference = 67.0 / 4096.0;
      index = 1; // Front Right
      key = String.format("SwerveDrive/wheel.%d", index);
      module = builder.wheelLocationMeters(new Translation2d(1, -1)).build();
      when(azimuthPositionStatusSig.getValueAsDouble()).thenReturn(expectedZeroReference);
      when(azimuthTalon.getRawPulseWidthPosition()).thenReturn(rawPulseWidthStatusSig);
      when(rawPulseWidthStatusSig.getValueAsDouble()).thenReturn(expectedZeroReference);
      module.storeAzimuthZeroReference();
      assertEquals(expectedZeroReference, Preferences.getDouble(key, -1));

      expectedZeroReference = -6767.0 / 4096.0;
      index = 2; // Back Left
      key = String.format("SwerveDrive/wheel.%d", index);
      module = builder.wheelLocationMeters(new Translation2d(-1, 1)).build();
      when(azimuthPositionStatusSig.getValueAsDouble()).thenReturn(expectedZeroReference);
      when(azimuthTalon.getRawPulseWidthPosition()).thenReturn(rawPulseWidthStatusSig);
      when(rawPulseWidthStatusSig.getValueAsDouble()).thenReturn(expectedZeroReference);
      module.storeAzimuthZeroReference();
      assertEquals(
          MathUtil.inputModulus(expectedZeroReference, 0.0, 1.0), Preferences.getDouble(key, -1));

      expectedZeroReference = 6727.0 / 4096.0;
      index = 3; // Back Right
      key = String.format("SwerveDrive/wheel.%d", index);
      module = builder.wheelLocationMeters(new Translation2d(-1, -1)).build();
      when(azimuthPositionStatusSig.getValueAsDouble()).thenReturn(expectedZeroReference);
      when(azimuthTalon.getRawPulseWidthPosition()).thenReturn(rawPulseWidthStatusSig);
      when(rawPulseWidthStatusSig.getValueAsDouble()).thenReturn(expectedZeroReference);
      module.storeAzimuthZeroReference();
      assertEquals(
          MathUtil.inputModulus(expectedZeroReference, 0.0, 1.0), Preferences.getDouble(key, -1));
    }

    @ParameterizedTest
    @CsvSource({"1e-6, 0, 1e-6", "1, 0, 1", "1.00024, 0, 0.00024", "1e-6, 0.67554, -0.67554"})
    @DisplayName("should set FX azimuth zero")
    void shouldSetFXAzimuthZero(
        double absoluteEncoderPosition, double zeroReference, double setpoint) {
      int index = 0; // Front Left
      String key = String.format("SwerveDrive/wheel.%d", index);
      Preferences.setDouble(key, zeroReference);

      when(azimuthTalon.getPosition()).thenReturn(azimuthPositionStatusSig);
      when(azimuthPositionStatusSig.getValueAsDouble()).thenReturn(absoluteEncoderPosition);
      when(azimuthTalon.getRawPulseWidthPosition()).thenReturn(rawPulseWidthStatusSig);
      when(rawPulseWidthStatusSig.getValueAsDouble()).thenReturn(absoluteEncoderPosition);
      module.loadAndSetAzimuthZeroReference();
      verify(azimuthTalon).setControl(posDutyCycleCaptor.capture());
      assertEquals(setpoint, posDutyCycleCaptor.getValue().Position, 0.01);
    }
  }

  @Nested
  @DisplayName(("V6 Should not Validate"))
  class V6TestShouldNotValidate {
    private TalonSRX azimuthTalon;
    private TalonFX driveTalon;

    @BeforeEach
    void setUp() {
      azimuthTalon = mock(TalonSRX.class);
      driveTalon = mock(TalonFX.class);
      when(driveTalon.setPosition(0)).thenReturn(StatusCode.OK);
    }

    @Test
    @DisplayName("when V6 Talon is null")
    void whenV6TalonIsNull() {
      var builder =
          new V6TalonSwerveModule.V6Builder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveGearRatio(kDriveGearRatio)
              .wheelDiameterInches(kWheelDiameterInches)
              .wheelLocationMeters(new Translation2d(1, 1))
              .driveDeadbandMetersPerSecond(kMaxSpeedMetersPerSecond);

      assertThrows(IllegalArgumentException.class, builder.azimuthTalon(null)::build);
    }

    @Test
    @DisplayName("when V6 drive gear ratio <= zero")
    void whenV6DriveGearRatioLteZero() {
      V6TalonSwerveModule.V6Builder builder =
          new V6TalonSwerveModule.V6Builder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .wheelDiameterInches(kWheelDiameterInches)
              .wheelLocationMeters(new Translation2d())
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond);
      assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    @DisplayName("when V6 wheel diameter <= zero")
    void whenV6WheelDiameterLteZero() {
      V6TalonSwerveModule.V6Builder builder =
          new V6TalonSwerveModule.V6Builder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveGearRatio(kDriveGearRatio)
              .wheelLocationMeters(new Translation2d())
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond);
      assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    @DisplayName("when V6 drive max m/s <= zero")
    void whenV6DriveMaximumMetersPerSecondLteZero() {
      V6TalonSwerveModule.V6Builder builder =
          new V6TalonSwerveModule.V6Builder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveGearRatio(kDriveGearRatio)
              .wheelLocationMeters(new Translation2d())
              .wheelDiameterInches(kWheelDiameterInches);
      assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    @DisplayName("when V6 wheel location not set")
    void whenV6WheelLocationNotSet() {
      V6TalonSwerveModule.V6Builder builder =
          new V6TalonSwerveModule.V6Builder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveGearRatio(kDriveGearRatio)
              .wheelDiameterInches(kWheelDiameterInches)
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond);
      assertThrows(IllegalArgumentException.class, builder::build);
    }
  }

  @Nested
  @DisplayName(("FX Should not Validate"))
  class FXTestShouldNotValidate {
    private TalonFXS azimuthTalon;
    private TalonFX driveTalon;

    @BeforeEach
    void setUp() {
      azimuthTalon = mock(TalonFXS.class);
      driveTalon = mock(TalonFX.class);
      when(driveTalon.setPosition(0)).thenReturn(StatusCode.OK);
    }

    @Test
    @DisplayName("when FX Talon is null")
    void whenFXTalonIsNull() {
      var builder =
          new FXSwerveModule.FXBuilder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveGearRatio(kDriveGearRatio)
              .wheelDiameterInches(kWheelDiameterInches)
              .wheelLocationMeters(new Translation2d(1, 1))
              .driveDeadbandMetersPerSecond(kMaxSpeedMetersPerSecond)
              .encoderOpposed(false);

      assertThrows(IllegalArgumentException.class, builder.azimuthTalon(null)::build);
    }

    @Test
    @DisplayName("when FX drive gear ratio <= zero")
    void whenFXDriveGearRatioLteZero() {
      FXSwerveModule.FXBuilder builder =
          new FXSwerveModule.FXBuilder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .wheelDiameterInches(kWheelDiameterInches)
              .wheelLocationMeters(new Translation2d())
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
              .encoderOpposed(false);
      assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    @DisplayName("when FX wheel diameter <= zero")
    void whenFXWheelDiameterLteZero() {
      FXSwerveModule.FXBuilder builder =
          new FXSwerveModule.FXBuilder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveGearRatio(kDriveGearRatio)
              .wheelLocationMeters(new Translation2d())
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
              .encoderOpposed(false);
      assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    @DisplayName("when V6 drive max m/s <= zero")
    void whenFXDriveMaximumMetersPerSecondLteZero() {
      FXSwerveModule.FXBuilder builder =
          new FXSwerveModule.FXBuilder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveGearRatio(kDriveGearRatio)
              .wheelLocationMeters(new Translation2d())
              .wheelDiameterInches(kWheelDiameterInches)
              .encoderOpposed(false);
      assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    @DisplayName("when FX wheel location not set")
    void whenFXWheelLocationNotSet() {
      FXSwerveModule.FXBuilder builder =
          new FXSwerveModule.FXBuilder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveGearRatio(kDriveGearRatio)
              .wheelDiameterInches(kWheelDiameterInches)
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
              .encoderOpposed(false);
      assertThrows(IllegalArgumentException.class, builder::build);
    }
  }

  @Nested
  @DisplayName("Should get V6 module state")
  class TestShouldGetV6ModuleState {
    private TalonSRX azimuthTalon;
    private TalonFX driveTalon;

    private StatusSignal<AngularVelocity> velocityStatusSig;
    private StatusSignal<Angle> positionStatusSig;

    @BeforeEach
    void setUp() {
      azimuthTalon = mock(TalonSRX.class);
      driveTalon = mock(TalonFX.class);
      when(driveTalon.setPosition(0)).thenReturn(StatusCode.OK);
      velocityStatusSig = (StatusSignal<AngularVelocity>) mock(StatusSignal.class);
      positionStatusSig = (StatusSignal<Angle>) mock(StatusSignal.class);
    }

    @Test
    @DisplayName("V6 drive speed with default encoder counts")
    void v6SpeedWithDefaultEncoderCounts() {
      V6TalonSwerveModule module =
          new V6TalonSwerveModule.V6Builder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveGearRatio(kDriveGearRatio)
              .wheelDiameterInches(kWheelDiameterInches)
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
              .wheelLocationMeters(new Translation2d())
              .build();
      //      when(driveTalon.getVelocity()).thenReturn(velocityStatusSig);
      Field driveVelField =
          ReflectionUtils.findFields(
                  V6TalonSwerveModule.class,
                  f -> f.getName().equals("driveVelocity"),
                  ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
              .get(0);
      driveVelField.setAccessible(true);
      try {
        driveVelField.set(module, velocityStatusSig);
      } catch (Exception e) {
        throw new IllegalArgumentException("Error: " + e);
      }
      when(velocityStatusSig.getValueAsDouble()).thenReturn(100.0);
      SwerveModuleState state = module.getState();
      assertEquals(3.657337448, state.speedMetersPerSecond, 1e-9);
    }

    @ParameterizedTest
    @CsvSource({"1, 100, 3.657337448", "2, 200, 3.657337448"})
    @DisplayName("V6 drive speed with encoder counts")
    void V6SpeedWithEncoderCounts(
        int driveCountsPerRev, double driveRotationsPerSec, double expectedMetersPerSecond) {
      V6TalonSwerveModule module =
          new V6TalonSwerveModule.V6Builder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveEncoderCountsPerRevolution(driveCountsPerRev)
              .driveGearRatio(kDriveGearRatio)
              .wheelDiameterInches(kWheelDiameterInches)
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
              .wheelLocationMeters(new Translation2d())
              .build();
      Field driveVelField =
          ReflectionUtils.findFields(
                  V6TalonSwerveModule.class,
                  f -> f.getName().equals("driveVelocity"),
                  ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
              .get(0);
      driveVelField.setAccessible(true);
      try {
        driveVelField.set(module, velocityStatusSig);
      } catch (Exception e) {
        throw new IllegalArgumentException("Error: " + e);
      }
      when(driveTalon.getVelocity()).thenReturn(velocityStatusSig);
      when(velocityStatusSig.getValueAsDouble()).thenReturn(driveRotationsPerSec);
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
    void V6AngleWithEncoderCounts(
        int azimuthEncoderCountsPerRevolution,
        double azimuthSelectedSensorPosition,
        double expectedAngleDeg) {
      V6TalonSwerveModule module =
          new V6TalonSwerveModule.V6Builder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveGearRatio(kDriveGearRatio)
              .azimuthEncoderCountsPerRevolution(azimuthEncoderCountsPerRevolution)
              .wheelDiameterInches(kWheelDiameterInches)
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
              .wheelLocationMeters(new Translation2d())
              .build();
      Field driveVelField =
          ReflectionUtils.findFields(
                  V6TalonSwerveModule.class,
                  f -> f.getName().equals("driveVelocity"),
                  ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
              .get(0);
      driveVelField.setAccessible(true);
      try {
        driveVelField.set(module, velocityStatusSig);
      } catch (Exception e) {
        throw new IllegalArgumentException("Error: " + e);
      }
      when(driveTalon.getVelocity()).thenReturn(velocityStatusSig);
      when(velocityStatusSig.getValueAsDouble()).thenReturn(0.0);
      when(azimuthTalon.getSelectedSensorPosition()).thenReturn(azimuthSelectedSensorPosition);
      SwerveModuleState state = module.getState();
      var expectedRotation = Rotation2d.fromDegrees(expectedAngleDeg);
      assertEquals(expectedRotation, state.angle);
    }

    @Test
    void V6GetMaxSpeedMetersPerSecond() {
      V6TalonSwerveModule module =
          new V6TalonSwerveModule.V6Builder()
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
    void V6GetWheelLocationMeters() {
      var expectedWheelLocation = new Translation2d(27, 67);
      V6TalonSwerveModule module =
          new V6TalonSwerveModule.V6Builder()
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
  @DisplayName("Should get FX module state")
  class TestShouldGetFXModuleState {
    private TalonFXS azimuthTalon;
    private TalonFX driveTalon;

    private StatusSignal<AngularVelocity> velocityStatusSig;
    private StatusSignal<Angle> positionStatusSig;
    private StatusSignal<Angle> azimuthPositionStatusSig;

    @BeforeEach
    void setUp() {
      azimuthTalon = mock(TalonFXS.class);
      driveTalon = mock(TalonFX.class);
      when(driveTalon.setPosition(0)).thenReturn(StatusCode.OK);
      velocityStatusSig = (StatusSignal<AngularVelocity>) mock(StatusSignal.class);
      positionStatusSig = (StatusSignal<Angle>) mock(StatusSignal.class);
      azimuthPositionStatusSig = (StatusSignal<Angle>) mock(StatusSignal.class);
    }

    @Test
    @DisplayName("FX drive speed with default encoder counts")
    void FXSpeedWithDefaultEncoderCounts() {
      FXSwerveModule module =
          new FXSwerveModule.FXBuilder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveGearRatio(kDriveGearRatio)
              .wheelDiameterInches(kWheelDiameterInches)
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
              .wheelLocationMeters(new Translation2d())
              .encoderOpposed(false)
              .build();
      //      when(driveTalon.getVelocity()).thenReturn(velocityStatusSig);
      Field driveVelField =
          ReflectionUtils.findFields(
                  FXSwerveModule.class,
                  f -> f.getName().equals("driveVelocity"),
                  ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
              .get(0);
      driveVelField.setAccessible(true);
      try {
        driveVelField.set(module, velocityStatusSig);
      } catch (Exception e) {
        throw new IllegalArgumentException("Error: " + e);
      }
      Field azimuthPosField =
          ReflectionUtils.findFields(
                  FXSwerveModule.class,
                  f -> f.getName().equals("azimuthPosition"),
                  ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
              .get(0);
      azimuthPosField.setAccessible(true);
      try {
        azimuthPosField.set(module, azimuthPositionStatusSig);
      } catch (Exception e) {
        throw new IllegalArgumentException("Error: " + e);
      }

      when(velocityStatusSig.getValueAsDouble()).thenReturn(100.0);
      SwerveModuleState state = module.getState();
      assertEquals(3.657337448, state.speedMetersPerSecond, 1e-9);
    }

    @ParameterizedTest
    @CsvSource({"1, 100, 3.657337448", "2, 200, 3.657337448"})
    @DisplayName("FX drive speed with encoder counts")
    void FXSpeedWithEncoderCounts(
        int driveCountsPerRev, double driveRotationsPerSec, double expectedMetersPerSecond) {
      FXSwerveModule module =
          new FXSwerveModule.FXBuilder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveEncoderCountsPerRevolution(driveCountsPerRev)
              .driveGearRatio(kDriveGearRatio)
              .wheelDiameterInches(kWheelDiameterInches)
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
              .wheelLocationMeters(new Translation2d())
              .encoderOpposed(false)
              .build();
      Field driveVelField =
          ReflectionUtils.findFields(
                  FXSwerveModule.class,
                  f -> f.getName().equals("driveVelocity"),
                  ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
              .get(0);
      driveVelField.setAccessible(true);
      try {
        driveVelField.set(module, velocityStatusSig);
      } catch (Exception e) {
        throw new IllegalArgumentException("Error: " + e);
      }

      Field azimuthPosField =
          ReflectionUtils.findFields(
                  FXSwerveModule.class,
                  f -> f.getName().equals("azimuthPosition"),
                  ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
              .get(0);
      azimuthPosField.setAccessible(true);
      try {
        azimuthPosField.set(module, azimuthPositionStatusSig);
      } catch (Exception e) {
        throw new IllegalArgumentException("Error: " + e);
      }
      when(driveTalon.getVelocity()).thenReturn(velocityStatusSig);
      when(velocityStatusSig.getValueAsDouble()).thenReturn(driveRotationsPerSec);
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
    void FXAngleWithEncoderCounts(
        int azimuthEncoderCountsPerRevolution,
        double azimuthSelectedSensorPosition,
        double expectedAngleDeg) {
      FXSwerveModule module =
          new FXSwerveModule.FXBuilder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveGearRatio(kDriveGearRatio)
              .azimuthEncoderCountsPerRevolution(azimuthEncoderCountsPerRevolution)
              .wheelDiameterInches(kWheelDiameterInches)
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
              .wheelLocationMeters(new Translation2d())
              .encoderOpposed(false)
              .build();
      Field driveVelField =
          ReflectionUtils.findFields(
                  FXSwerveModule.class,
                  f -> f.getName().equals("driveVelocity"),
                  ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
              .get(0);
      driveVelField.setAccessible(true);
      try {
        driveVelField.set(module, velocityStatusSig);
      } catch (Exception e) {
        throw new IllegalArgumentException("Error: " + e);
      }
      Field azimuthPosField =
          ReflectionUtils.findFields(
                  FXSwerveModule.class,
                  f -> f.getName().equals("azimuthPosition"),
                  ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
              .get(0);
      azimuthPosField.setAccessible(true);
      try {
        azimuthPosField.set(module, azimuthPositionStatusSig);
      } catch (Exception e) {
        throw new IllegalArgumentException("Error: " + e);
      }
      when(driveTalon.getVelocity()).thenReturn(velocityStatusSig);
      when(velocityStatusSig.getValueAsDouble()).thenReturn(0.0);
      when(azimuthTalon.getPosition()).thenReturn(azimuthPositionStatusSig);
      when(azimuthPositionStatusSig.getValueAsDouble()).thenReturn(azimuthSelectedSensorPosition);
      SwerveModuleState state = module.getState();
      var expectedRotation = Rotation2d.fromDegrees(expectedAngleDeg);
      assertEquals(expectedRotation, state.angle);
    }

    @Test
    void FXGetMaxSpeedMetersPerSecond() {
      FXSwerveModule module =
          new FXSwerveModule.FXBuilder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveGearRatio(kDriveGearRatio)
              .wheelDiameterInches(kWheelDiameterInches)
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
              .wheelLocationMeters(new Translation2d())
              .encoderOpposed(false)
              .build();
      assertEquals(kMaxSpeedMetersPerSecond, module.getMaxSpeedMetersPerSecond());
    }

    @Test
    void FXGetWheelLocationMeters() {
      var expectedWheelLocation = new Translation2d(27, 67);
      FXSwerveModule module =
          new FXSwerveModule.FXBuilder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveGearRatio(kDriveGearRatio)
              .wheelDiameterInches(kWheelDiameterInches)
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
              .wheelLocationMeters(expectedWheelLocation)
              .encoderOpposed(false)
              .build();
      assertEquals(expectedWheelLocation, module.getWheelLocationMeters());
    }
  }

  @Nested
  @DisplayName("Should set V6 Module State")
  class TestShouldSetV6ModuleState {
    final ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
    final ArgumentCaptor<VelocityDutyCycle> velDutyCycleCaptor =
        ArgumentCaptor.forClass(VelocityDutyCycle.class);
    final ArgumentCaptor<DutyCycleOut> dutyCycleCaptor =
        ArgumentCaptor.forClass(DutyCycleOut.class);
    private TalonSRX azimuthTalon;
    private TalonFX driveTalon;

    @BeforeEach
    void setUp() {
      azimuthTalon = mock(TalonSRX.class);
      driveTalon = mock(TalonFX.class);
      when(driveTalon.setPosition(0)).thenReturn(StatusCode.OK);
    }

    @ParameterizedTest
    @CsvSource({
      // drive counts/rev, drive m/s, percent out
      "1, 0, 0",
      "1, 0.304778121, 0.079365079",
      "1, 3.657337448, 0.9523809525",
      "2, 0, 0",
      "2, 0.304778121, 0.079365079",
      "2, 3.657337448, 0.9523809525"
    })
    @DisplayName("V6 drive speed when open loop")
    void driveOpenLoopSpeed(
        int driveEncoderCountsPerRevolution,
        double driveMetersPerSecond,
        double expectedPercentOutput) {
      V6TalonSwerveModule module =
          new V6TalonSwerveModule.V6Builder()
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
      verify(driveTalon).setControl(dutyCycleCaptor.capture());
      assertEquals(expectedPercentOutput, dutyCycleCaptor.getValue().Output, 1e-9);
    }

    @ParameterizedTest
    @CsvSource({
      // Drive count/rev, drive m/s, drive setpoint
      "1, 0, 0",
      "1, 0.182867238, 5",
      "1, 1.097203429, 30",
      "1, 2.925875810, 80",
      "2, 0, 0",
      "2, 0.182867238, 10",
      "2, 1.097203429, 60",
      "2, 2.92587510, 160"
    })
    @DisplayName("V6 drive speed when closed loop")
    void V6DriveClosedLoopSpeed(
        int driveEncoderCountsPerRevolution, double driveMetersPerSecond, double expectedSetpoint) {
      V6TalonSwerveModule module =
          new V6TalonSwerveModule.V6Builder()
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
      verify(driveTalon).setControl(velDutyCycleCaptor.capture());
      assertEquals(expectedSetpoint, velDutyCycleCaptor.getValue().Velocity, 0.75);
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
    @DisplayName("V6 azimuth angle when open loop")
    void V6AzimuthAngleWhenOpenLoop(
        int azimuthEncoderCountsPerRevolution,
        double countsBefore,
        double angleDegrees,
        double countsExpected,
        boolean reversed) {

      var speedMetersPerSecond = 3.657337448;
      var drivePercentOutput = 0.9523809525;

      V6TalonSwerveModule module =
          new V6TalonSwerveModule.V6Builder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .azimuthEncoderCountsPerRevolution(azimuthEncoderCountsPerRevolution)
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
      verify(driveTalon).setControl(dutyCycleCaptor.capture());
      assertEquals(
          reversed ? -drivePercentOutput : drivePercentOutput,
          dutyCycleCaptor.getValue().Output,
          1e-6);
    }
  }

  @Nested
  @DisplayName("Should set FX Module State")
  class TestShouldSetFXModuleState {
    final ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
    final ArgumentCaptor<VelocityDutyCycle> velDutyCycleCaptor =
        ArgumentCaptor.forClass(VelocityDutyCycle.class);
    final ArgumentCaptor<MotionMagicDutyCycle> posDutyCycleCaptor =
        ArgumentCaptor.forClass(MotionMagicDutyCycle.class);
    final ArgumentCaptor<DutyCycleOut> dutyCycleCaptor =
        ArgumentCaptor.forClass(DutyCycleOut.class);
    private TalonFXS azimuthTalon;
    private TalonFX driveTalon;

    private StatusSignal<Angle> azimuthPosStatusSig;

    @BeforeEach
    void setUp() {
      azimuthTalon = mock(TalonFXS.class);
      driveTalon = mock(TalonFX.class);
      when(driveTalon.setPosition(0)).thenReturn(StatusCode.OK);
      azimuthPosStatusSig = (StatusSignal<Angle>) mock(StatusSignal.class);
    }

    @ParameterizedTest
    @CsvSource({
      // drive counts/rev, drive m/s, percent out
      "1, 0, 0",
      "1, 0.304778121, 0.079365079",
      "1, 3.657337448, 0.9523809525",
      "2, 0, 0",
      "2, 0.304778121, 0.079365079",
      "2, 3.657337448, 0.9523809525"
    })
    @DisplayName("FX drive speed when open loop")
    void driveOpenLoopSpeed(
        int driveEncoderCountsPerRevolution,
        double driveMetersPerSecond,
        double expectedPercentOutput) {
      FXSwerveModule module =
          new FXSwerveModule.FXBuilder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveEncoderCountsPerRevolution(driveEncoderCountsPerRevolution)
              .driveGearRatio(kDriveGearRatio)
              .wheelDiameterInches(kWheelDiameterInches)
              .wheelLocationMeters(new Translation2d())
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
              .encoderOpposed(false)
              .build();
      Field azimuthPosField =
          ReflectionUtils.findFields(
                  FXSwerveModule.class,
                  f -> f.getName().equals("azimuthPosition"),
                  ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
              .get(0);
      azimuthPosField.setAccessible(true);
      try {
        azimuthPosField.set(module, azimuthPosStatusSig);
      } catch (Exception e) {
        throw new IllegalArgumentException("Error: " + e);
      }
      when(azimuthPosStatusSig.getValueAsDouble()).thenReturn(0.0);
      var desiredState = new SwerveModuleState(driveMetersPerSecond, new Rotation2d());
      module.setDesiredState(desiredState, true);
      verify(driveTalon).setControl(dutyCycleCaptor.capture());
      assertEquals(expectedPercentOutput, dutyCycleCaptor.getValue().Output, 1e-9);
    }

    @ParameterizedTest
    @CsvSource({
      // Drive count/rev, drive m/s, drive setpoint
      "1, 0, 0",
      "1, 0.182867238, 5",
      "1, 1.097203429, 30",
      "1, 2.925875810, 80",
      "2, 0, 0",
      "2, 0.182867238, 10",
      "2, 1.097203429, 60",
      "2, 2.92587510, 160"
    })
    @DisplayName("FX drive speed when closed loop")
    void FXDriveClosedLoopSpeed(
        int driveEncoderCountsPerRevolution, double driveMetersPerSecond, double expectedSetpoint) {
      FXSwerveModule module =
          new FXSwerveModule.FXBuilder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveEncoderCountsPerRevolution(driveEncoderCountsPerRevolution)
              .driveGearRatio(kDriveGearRatio)
              .wheelDiameterInches(kWheelDiameterInches)
              .wheelLocationMeters(new Translation2d())
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
              .encoderOpposed(false)
              .build();
      Field azimuthPosField =
          ReflectionUtils.findFields(
                  FXSwerveModule.class,
                  f -> f.getName().equals("azimuthPosition"),
                  ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
              .get(0);
      azimuthPosField.setAccessible(true);
      try {
        azimuthPosField.set(module, azimuthPosStatusSig);
      } catch (Exception e) {
        throw new IllegalArgumentException("Error: " + e);
      }
      when(azimuthPosStatusSig.getValueAsDouble()).thenReturn(0.0);
      var desiredState = new SwerveModuleState(driveMetersPerSecond, new Rotation2d());
      module.setDesiredState(desiredState, false);
      verify(driveTalon).setControl(velDutyCycleCaptor.capture());
      assertEquals(expectedSetpoint, velDutyCycleCaptor.getValue().Velocity, 0.75);
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
    @DisplayName("FX azimuth angle when open loop")
    void FXAzimuthAngleWhenOpenLoop(
        int azimuthEncoderCountsPerRevolution,
        double countsBefore,
        double angleDegrees,
        double countsExpected,
        boolean reversed) {

      var speedMetersPerSecond = 3.657337448;
      var drivePercentOutput = 0.9523809525;

      FXSwerveModule module =
          new FXSwerveModule.FXBuilder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .azimuthEncoderCountsPerRevolution(azimuthEncoderCountsPerRevolution)
              .driveGearRatio(kDriveGearRatio)
              .wheelDiameterInches(kWheelDiameterInches)
              .wheelLocationMeters(new Translation2d())
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
              .encoderOpposed(false)
              .build();
      var desiredState =
          new SwerveModuleState(speedMetersPerSecond, Rotation2d.fromDegrees(angleDegrees));

      Field azimuthPosField =
          ReflectionUtils.findFields(
                  FXSwerveModule.class,
                  f -> f.getName().equals("azimuthPosition"),
                  ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
              .get(0);
      azimuthPosField.setAccessible(true);
      try {
        azimuthPosField.set(module, azimuthPosStatusSig);
      } catch (Exception e) {
        throw new IllegalArgumentException("Error: " + e);
      }
      when(azimuthPosStatusSig.getValueAsDouble()).thenReturn(countsBefore);

      module.setDesiredState(desiredState, true);
      verify(azimuthTalon).setControl(posDutyCycleCaptor.capture());
      //      verify(azimuthTalon).set(eq(ControlMode.MotionMagic), captor.capture());
      assertEquals(countsExpected, posDutyCycleCaptor.getValue().Position, 0.5);
      verify(driveTalon).setControl(dutyCycleCaptor.capture());
      assertEquals(
          reversed ? -drivePercentOutput : drivePercentOutput,
          dutyCycleCaptor.getValue().Output,
          1e-6);
    }
  }

  @Nested
  @DisplayName("Should get V6 module position")
  class TestShouldGetV6ModulePosition {
    private TalonSRX azimuthTalon;
    private TalonFX driveTalon;

    private StatusSignal<Angle> positionStatusSig;

    private Timestamp timestamp;

    private StatusSignal<AngularVelocity> velocityStatusSig;

    @BeforeEach
    void setTup() {
      azimuthTalon = mock(TalonSRX.class);
      driveTalon = mock(TalonFX.class);
      when(driveTalon.setPosition(0)).thenReturn(StatusCode.OK);
      positionStatusSig = (StatusSignal<Angle>) mock(StatusSignal.class);
      timestamp = mock(Timestamp.class);
      velocityStatusSig = (StatusSignal<AngularVelocity>) mock(StatusSignal.class);
    }

    @Test
    @DisplayName("V6 drive position with default encoder counts")
    void V6PositionWithDefaultEncoderCounts() {
      V6TalonSwerveModule module =
          new V6TalonSwerveModule.V6Builder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveGearRatio(kDriveGearRatio)
              .wheelDiameterInches(kWheelDiameterInches)
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
              .wheelLocationMeters(new Translation2d())
              .build();
      Field drivePosField =
          ReflectionUtils.findFields(
                  V6TalonSwerveModule.class,
                  f -> f.getName().equals("drivePosition"),
                  ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
              .get(0);
      Field driveVelField =
          ReflectionUtils.findFields(
                  V6TalonSwerveModule.class,
                  f -> f.getName().equals("driveVelocity"),
                  ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
              .get(0);

      drivePosField.setAccessible(true);
      driveVelField.setAccessible(true);
      try {
        drivePosField.set(module, positionStatusSig);
      } catch (Exception e) {
        throw new IllegalArgumentException("Error: " + e);
      }
      try {
        driveVelField.set(module, velocityStatusSig);
      } catch (Exception e) {
        throw new IllegalArgumentException("Error: " + e);
      }
      //      field.set(module, positionStatusSig);
      when(driveTalon.getPosition()).thenReturn(positionStatusSig);
      //      when(drivePosition.getLatency()).thenReturn(0.0);
      when(positionStatusSig.getValueAsDouble()).thenReturn(10.0);
      when(positionStatusSig.getValue()).thenReturn(Rotations.of(10.0));
      when(positionStatusSig.getTimestamp()).thenReturn(timestamp);
      when(timestamp.getLatency()).thenReturn(0.0);
      when(driveTalon.getVelocity()).thenReturn(velocityStatusSig);
      when(velocityStatusSig.getValueAsDouble()).thenReturn(0.0);
      SwerveModulePosition position = module.getPosition();
      assertEquals(0.365733744755412, position.distanceMeters, 1e-9);
    }

    @ParameterizedTest
    @CsvSource({"1, 10, 0.365733744755412", "2, 20, 0.365733744755412"})
    @DisplayName("V6 drive position with encoder counts")
    void V6PositionWithEncoderCounts(
        int driveEncoderCountsPerRevolution, double driveTalonPosition, double expectedMeters) {
      V6TalonSwerveModule module =
          new V6TalonSwerveModule.V6Builder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveEncoderCountsPerRevolution(driveEncoderCountsPerRevolution)
              .driveGearRatio(kDriveGearRatio)
              .wheelDiameterInches(kWheelDiameterInches)
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
              .wheelLocationMeters(new Translation2d())
              .build();
      Field drivePosField =
          ReflectionUtils.findFields(
                  V6TalonSwerveModule.class,
                  f -> f.getName().equals("drivePosition"),
                  ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
              .get(0);
      Field driveVelField =
          ReflectionUtils.findFields(
                  V6TalonSwerveModule.class,
                  f -> f.getName().equals("driveVelocity"),
                  ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
              .get(0);

      drivePosField.setAccessible(true);
      driveVelField.setAccessible(true);

      try {
        drivePosField.set(module, positionStatusSig);
      } catch (Exception e) {
        throw new IllegalArgumentException("Error: " + e);
      }

      try {
        driveVelField.set(module, velocityStatusSig);
      } catch (Exception e) {
        throw new IllegalArgumentException("Error: " + e);
      }

      when(driveTalon.getPosition()).thenReturn(positionStatusSig);
      when(positionStatusSig.getValue()).thenReturn(Rotations.of(driveTalonPosition));
      when(positionStatusSig.getValueAsDouble()).thenReturn(driveTalonPosition);

      when(positionStatusSig.getTimestamp()).thenReturn(timestamp);
      when(timestamp.getLatency()).thenReturn(0.0);
      when(driveTalon.getVelocity()).thenReturn(velocityStatusSig);
      when(velocityStatusSig.getValueAsDouble()).thenReturn(0.0);
      SwerveModulePosition position = module.getPosition();
      assertEquals(expectedMeters, position.distanceMeters, 1e-9);
    }
  }

  @Nested
  @DisplayName("Should get FX module position")
  class TestShouldGetFXModulePosition {
    private TalonFXS azimuthTalon;
    private TalonFX driveTalon;

    private StatusSignal<Angle> positionStatusSig;

    private Timestamp timestamp;

    private StatusSignal<AngularVelocity> velocityStatusSig;
    private StatusSignal<Angle> azimuthPosStatusSig;

    @BeforeEach
    void setTup() {
      azimuthTalon = mock(TalonFXS.class);
      driveTalon = mock(TalonFX.class);
      when(driveTalon.setPosition(0)).thenReturn(StatusCode.OK);
      positionStatusSig = (StatusSignal<Angle>) mock(StatusSignal.class);
      timestamp = mock(Timestamp.class);
      velocityStatusSig = (StatusSignal<AngularVelocity>) mock(StatusSignal.class);
      azimuthPosStatusSig = (StatusSignal<Angle>) mock(StatusSignal.class);
    }

    @Test
    @DisplayName("FX drive position with default encoder counts")
    void FXPositionWithDefaultEncoderCounts() {
      FXSwerveModule module =
          new FXSwerveModule.FXBuilder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveGearRatio(kDriveGearRatio)
              .wheelDiameterInches(kWheelDiameterInches)
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
              .wheelLocationMeters(new Translation2d())
              .encoderOpposed(false)
              .build();
      Field drivePosField =
          ReflectionUtils.findFields(
                  FXSwerveModule.class,
                  f -> f.getName().equals("drivePosition"),
                  ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
              .get(0);
      Field driveVelField =
          ReflectionUtils.findFields(
                  FXSwerveModule.class,
                  f -> f.getName().equals("driveVelocity"),
                  ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
              .get(0);

      Field azimuthPosField =
          ReflectionUtils.findFields(
                  FXSwerveModule.class,
                  f -> f.getName().equals("azimuthPosition"),
                  ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
              .get(0);
      azimuthPosField.setAccessible(true);
      try {
        azimuthPosField.set(module, azimuthPosStatusSig);
      } catch (Exception e) {
        throw new IllegalArgumentException("Error: " + e);
      }

      drivePosField.setAccessible(true);
      driveVelField.setAccessible(true);
      try {
        drivePosField.set(module, positionStatusSig);
      } catch (Exception e) {
        throw new IllegalArgumentException("Error: " + e);
      }
      try {
        driveVelField.set(module, velocityStatusSig);
      } catch (Exception e) {
        throw new IllegalArgumentException("Error: " + e);
      }
      //      field.set(module, positionStatusSig);
      when(driveTalon.getPosition()).thenReturn(positionStatusSig);
      //      when(drivePosition.getLatency()).thenReturn(0.0);
      when(positionStatusSig.getValueAsDouble()).thenReturn(10.0);
      when(positionStatusSig.getValue()).thenReturn(Rotations.of(10.0));
      when(positionStatusSig.getTimestamp()).thenReturn(timestamp);
      when(timestamp.getLatency()).thenReturn(0.0);
      when(driveTalon.getVelocity()).thenReturn(velocityStatusSig);
      when(velocityStatusSig.getValueAsDouble()).thenReturn(0.0);
      SwerveModulePosition position = module.getPosition();
      assertEquals(0.365733744755412, position.distanceMeters, 1e-9);
    }

    @ParameterizedTest
    @CsvSource({"1, 10, 0.365733744755412", "2, 20, 0.365733744755412"})
    @DisplayName("FX drive position with encoder counts")
    void FXPositionWithEncoderCounts(
        int driveEncoderCountsPerRevolution, double driveTalonPosition, double expectedMeters) {
      FXSwerveModule module =
          new FXSwerveModule.FXBuilder()
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .driveEncoderCountsPerRevolution(driveEncoderCountsPerRevolution)
              .driveGearRatio(kDriveGearRatio)
              .wheelDiameterInches(kWheelDiameterInches)
              .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
              .wheelLocationMeters(new Translation2d())
              .encoderOpposed(false)
              .build();
      Field drivePosField =
          ReflectionUtils.findFields(
                  FXSwerveModule.class,
                  f -> f.getName().equals("drivePosition"),
                  ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
              .get(0);
      Field driveVelField =
          ReflectionUtils.findFields(
                  FXSwerveModule.class,
                  f -> f.getName().equals("driveVelocity"),
                  ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
              .get(0);

      drivePosField.setAccessible(true);
      driveVelField.setAccessible(true);

      try {
        drivePosField.set(module, positionStatusSig);
      } catch (Exception e) {
        throw new IllegalArgumentException("Error: " + e);
      }

      try {
        driveVelField.set(module, velocityStatusSig);
      } catch (Exception e) {
        throw new IllegalArgumentException("Error: " + e);
      }

      Field azimuthPosField =
          ReflectionUtils.findFields(
                  FXSwerveModule.class,
                  f -> f.getName().equals("azimuthPosition"),
                  ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
              .get(0);
      azimuthPosField.setAccessible(true);
      try {
        azimuthPosField.set(module, azimuthPosStatusSig);
      } catch (Exception e) {
        throw new IllegalArgumentException("Error: " + e);
      }

      when(driveTalon.getPosition()).thenReturn(positionStatusSig);
      when(positionStatusSig.getValue()).thenReturn(Rotations.of(driveTalonPosition));
      when(positionStatusSig.getValueAsDouble()).thenReturn(driveTalonPosition);

      when(positionStatusSig.getTimestamp()).thenReturn(timestamp);
      when(timestamp.getLatency()).thenReturn(0.0);
      when(driveTalon.getVelocity()).thenReturn(velocityStatusSig);
      when(velocityStatusSig.getValueAsDouble()).thenReturn(0.0);
      SwerveModulePosition position = module.getPosition();
      assertEquals(expectedMeters, position.distanceMeters, 1e-9);
    }
  }
}
