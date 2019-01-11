package org.strykeforce.thirdcoast.swerve;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.byLessThan;
import static org.mockito.Mockito.*;
import static org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode.TELEOP;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.Preferences;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SwerveDriveTest {

  @Mock private Wheel wheel0, wheel1, wheel2, wheel3;
  private Wheel[] wheels;
  private SwerveDriveConfig config = new SwerveDriveConfig();
  @Mock private AHRS gyro;

  @BeforeEach
  void setuUp() {
    wheels = new Wheel[] {wheel0, wheel1, wheel2, wheel3};
    config.wheels = wheels;
    when(gyro.isConnected()).thenReturn(true);
    config.gyro = gyro;
  }

  @Test
  void getPreferenceKeyForWheel() {
    assertThat(SwerveDrive.getPreferenceKeyForWheel(3).equals("SwerveDrive/wheel.3"));
    // avoid UnnecessaryStubbingException for gyro.isConnected()
    SwerveDrive swerve = new SwerveDrive(config);
  }

  @Test
  void getWheels() {
    SwerveDrive swerve = new SwerveDrive(config);
    assertThat(swerve.getWheels()).isSameAs(wheels);
  }

  @Test
  void setDriveMode() {
    SwerveDrive swerve = new SwerveDrive(config);
    swerve.setDriveMode(TELEOP);
    for (Wheel wheel : wheels) verify(wheel).setDriveMode(TELEOP);
  }

  @Test
  void set() {
    SwerveDrive swerve = new SwerveDrive(config);
    swerve.set(0.27, 0.67);
    for (Wheel wheel : wheels) verify(wheel).set(0.27, 0.67);
  }

  @Test
  void stop() {
    SwerveDrive swerve = new SwerveDrive(config);
    swerve.stop();
    for (Wheel wheel : wheels) verify(wheel).stop();
  }

  @Test
  void saveAzimuthPositions(@Mock Preferences prefs) {
    SwerveDrive swerve = new SwerveDrive(config);
    for (int i = 0; i < 4; i++) when(wheels[i].getAzimuthAbsolutePosition()).thenReturn(i);
    swerve.saveAzimuthPositions(prefs);
    for (int i = 0; i < 4; i++) {
      String key = SwerveDrive.getPreferenceKeyForWheel(i);
      verify(prefs).putInt(key, i);
    }
  }

  @Test
  void zeroAzimuthEncoders(@Mock Preferences prefs) {
    SwerveDrive swerve = new SwerveDrive(config);
    for (int i = 0; i < 4; i++) {
      String key = SwerveDrive.getPreferenceKeyForWheel(i);
      doReturn(i).when(prefs).getInt(key, SwerveDrive.DEFAULT_ABSOLUTE_AZIMUTH_OFFSET);
    }
    swerve.zeroAzimuthEncoders(prefs);

    for (int i = 0; i < 4; i++) {
      verify(wheels[i]).setAzimuthZero(i);
    }
  }

  @Test
  void getGyro() {
    SwerveDrive swerve = new SwerveDrive(config);
    assertThat(swerve.getGyro()).isSameAs(gyro);
  }

  @Test
  void getLengthComponent() {
    config.length = 1;
    config.width = 2;
    SwerveDrive swerve = new SwerveDrive(config);
    assertThat(swerve.getLengthComponent()).isEqualTo(1 / Math.sqrt(5));
  }

  @Test
  void getWidthComponent() {
    config.length = 1;
    config.width = 2;
    SwerveDrive swerve = new SwerveDrive(config);
    assertThat(swerve.getWidthComponent()).isEqualTo(2 / Math.sqrt(5));
  }

  @ParameterizedTest
  @CsvFileSource(resources = "/swervedrive_drive_cases.csv", numLinesToSkip = 1)
  void drive(
      double forward,
      double strafe,
      double yaw,
      double wheel0Azimuth,
      double wheel0Drive,
      double wheel1Azimuth,
      double wheel1Drive,
      double wheel2Azimuth,
      double wheel2Drive,
      double wheel3Azimuth,
      double wheel3Drive) {
    SwerveDrive swerve = new SwerveDrive(config);

    ArgumentCaptor<Double> wheel0AzimuthArg = ArgumentCaptor.forClass(Double.class);
    ArgumentCaptor<Double> wheel0DriveArg = ArgumentCaptor.forClass(Double.class);
    ArgumentCaptor<Double> wheel1AzimuthArg = ArgumentCaptor.forClass(Double.class);
    ArgumentCaptor<Double> wheel1DriveArg = ArgumentCaptor.forClass(Double.class);
    ArgumentCaptor<Double> wheel2AzimuthArg = ArgumentCaptor.forClass(Double.class);
    ArgumentCaptor<Double> wheel2DriveArg = ArgumentCaptor.forClass(Double.class);
    ArgumentCaptor<Double> wheel3AzimuthArg = ArgumentCaptor.forClass(Double.class);
    ArgumentCaptor<Double> wheel3DriveArg = ArgumentCaptor.forClass(Double.class);

    swerve.drive(forward, strafe, yaw);

    verify(wheel0).set(wheel0AzimuthArg.capture(), wheel0DriveArg.capture());
    verify(wheel1).set(wheel1AzimuthArg.capture(), wheel1DriveArg.capture());
    verify(wheel2).set(wheel2AzimuthArg.capture(), wheel2DriveArg.capture());
    verify(wheel3).set(wheel3AzimuthArg.capture(), wheel3DriveArg.capture());

    double tol = 1.5e-4;
    assertThat(wheel0AzimuthArg.getValue()).isCloseTo(wheel0Azimuth, byLessThan(tol));
    assertThat(wheel0DriveArg.getValue()).isCloseTo(wheel0Drive, byLessThan(tol));
    assertThat(wheel1AzimuthArg.getValue()).isCloseTo(wheel1Azimuth, byLessThan(tol));
    assertThat(wheel1DriveArg.getValue()).isCloseTo(wheel1Drive, byLessThan(tol));
    assertThat(wheel2AzimuthArg.getValue()).isCloseTo(wheel2Azimuth, byLessThan(tol));
    assertThat(wheel2DriveArg.getValue()).isCloseTo(wheel2Drive, byLessThan(tol));
    assertThat(wheel3AzimuthArg.getValue()).isCloseTo(wheel3Azimuth, byLessThan(tol));
    assertThat(wheel3DriveArg.getValue()).isCloseTo(wheel3Drive, byLessThan(tol));
  }
}
