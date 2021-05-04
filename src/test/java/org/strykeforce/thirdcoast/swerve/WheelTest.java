package org.strykeforce.thirdcoast.swerve;

import static com.ctre.phoenix.motorcontrol.ControlMode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.byLessThan;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;
import static org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode.*;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WheelTest {

  @Mock private BaseTalon driveTalon;
  @Mock private TalonSRX azimuthTalon;

  static Stream<Arguments> setDriveModeTestProvider() {
    return Stream.of(
        arguments(OPEN_LOOP, PercentOutput, 27.0),
        arguments(TELEOP, PercentOutput, 27.0),
        arguments(CLOSED_LOOP, Velocity, 270.0),
        arguments(TRAJECTORY, Velocity, 270.0),
        arguments(AZIMUTH, Velocity, 270.0));
  }

  @ParameterizedTest
  @CsvFileSource(resources = "/wheel_set_cases.csv", numLinesToSkip = 1)
  void set(double startPosition, double setpoint, double endPosition, boolean isReversed) {
    Wheel wheel = new Wheel(azimuthTalon, driveTalon, 1.0);
    double encoderStartingPosition = Math.round(startPosition * 4096d);
    when(azimuthTalon.getSelectedSensorPosition(0)).thenReturn(encoderStartingPosition);
    wheel.set(setpoint, 1.0);

    ArgumentCaptor<Double> argument = ArgumentCaptor.forClass(Double.class);
    verify(azimuthTalon).set((ControlMode) any(), argument.capture());
    assertThat(argument.getValue()).isCloseTo(endPosition * 4096d, byLessThan(1e-12));
    verify(driveTalon).set(PercentOutput, isReversed ? -1d : 1d);
  }

  @Test
  void zeroDriveLeavesAzimuthAlone() {
    Wheel wheel = new Wheel(azimuthTalon, driveTalon, 1.0);
    wheel.set(0d, 0d);
    verify(driveTalon).set(PercentOutput, 0d);
    verify(azimuthTalon, never()).set((TalonSRXControlMode) any(), anyDouble());
  }

  @Test
  void setAzimuthPosition() {
    Wheel wheel = new Wheel(azimuthTalon, driveTalon, 1.0);
    wheel.setAzimuthPosition(2767);
    verify(azimuthTalon).set(MotionMagic, 2767);
  }

  @Test
  void disableAzimuth() {
    Wheel wheel = new Wheel(azimuthTalon, driveTalon, 1.0);
    wheel.disableAzimuth();
    verify(azimuthTalon).neutralOutput();
  }

  @ParameterizedTest
  @MethodSource("setDriveModeTestProvider")
  void setDriveMode(SwerveDrive.DriveMode driveMode, ControlMode controlMode, double setpoint) {
    Wheel wheel = new Wheel(azimuthTalon, driveTalon, 10.0);
    wheel.setDriveMode(driveMode);
    wheel.set(0.0, 27.0);
    verify(driveTalon).set(controlMode, setpoint);
  }

  @Test
  void stopOpenLoop() {
    Wheel wheel = new Wheel(azimuthTalon, driveTalon, 1.0);
    when(azimuthTalon.getSelectedSensorPosition(0)).thenReturn(2767.0);
    wheel.setDriveMode(OPEN_LOOP);
    wheel.stop();
    wheel.setDriveMode(CLOSED_LOOP);
    wheel.stop();

    verify(azimuthTalon, times(2)).set(MotionMagic, 2767);
    verify(driveTalon).set(PercentOutput, 0.0);
    verify(driveTalon).set(Velocity, 0.0);
  }

  @ParameterizedTest
  @CsvSource({"0, 2767, -2767"})
  void setAzimuthZero(
      int encoderPosition, int zero, int setpoint, @Mock SensorCollection sensorCollection) {
    Wheel wheel = new Wheel(azimuthTalon, driveTalon, 1.0);
    when(sensorCollection.getPulseWidthPosition()).thenReturn(encoderPosition);
    when(azimuthTalon.getSensorCollection()).thenReturn(sensorCollection);

    wheel.setAzimuthZero(zero);
    verify(azimuthTalon).setSelectedSensorPosition(setpoint, 0, 10);
  }

  @ParameterizedTest
  @CsvSource({"2048, 2048", "6144, 2048", "63488, 2048", "-1045504, 3072"})
  void getAzimuthAbsolutePosition(
      int encoderPosition, int absolutePosition, @Mock SensorCollection sensorCollection) {
    Wheel wheel = new Wheel(azimuthTalon, driveTalon, 1.0);
    when(sensorCollection.getPulseWidthPosition()).thenReturn(encoderPosition);
    when(azimuthTalon.getSensorCollection()).thenReturn(sensorCollection);
    assertThat(wheel.getAzimuthAbsolutePosition()).isEqualTo(absolutePosition);
  }

  @Test
  void getAzimuthTalon() {
    Wheel wheel = new Wheel(azimuthTalon, driveTalon, 2767.0);
    assertThat(wheel.getAzimuthTalon()).isSameAs(azimuthTalon);
  }

  @Test
  void getDriveTalon() {
    Wheel wheel = new Wheel(azimuthTalon, driveTalon, 2767.0);
    assertThat(wheel.getDriveTalon()).isSameAs(driveTalon);
  }

  @Test
  void getDriveSetpointMax() {
    Wheel wheel = new Wheel(azimuthTalon, driveTalon, 2767.0);
    assertThat(wheel.getDriveSetpointMax()).isEqualTo(2767.0);
  }
}
