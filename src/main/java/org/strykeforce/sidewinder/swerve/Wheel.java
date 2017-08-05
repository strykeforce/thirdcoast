package org.strykeforce.sidewinder.swerve;

import com.ctre.CANTalon;
import org.strykeforce.sidewinder.talon.TalonParameters;

/**
 * Represents a swerve drive wheel azimuth and drive motors.
 */
class Wheel {

  private final CANTalon azimuth;
  private final CANTalon drive;

  /**
   * This designated constructor constructs a wheel by supplying azimuth and drive talons. They are
   * initialized with Talon configurations named "azimuth" and "drive" respectively. Assumes the
   * Talon configurations have been registered.
   *
   * @param azimuth the azimuth CANTalon
   * @param drive the drive CANTalon
   * @see org.strykeforce.sidewinder.talon.TalonParameters#register(String)
   */
  Wheel(CANTalon azimuth, CANTalon drive) {
    String AZIMUTH_PARAMETERS = "azimuth";
    String DRIVE_PARAMETERS = "drive";

    this.azimuth = azimuth;
    this.drive = drive;
    setAzimuthParameters(AZIMUTH_PARAMETERS);
    setDriveParameters(DRIVE_PARAMETERS);
  }

  /**
   * Convenience constructor for a wheel by specifying the wheel number (0-3).
   *
   * @param index the wheel number
   */
  Wheel(int index) {
    this(new CANTalon(index), new CANTalon(index + 10));
  }

  void set(double azimuth, double drive) {
    this.azimuth.set(azimuth);
    this.drive.set(drive);
  }

  void stop() {
    azimuth.set(azimuth.getPosition());
    drive.set(0);
  }

  void setAzimuthParameters(String parameters) {
    TalonParameters talonParameters = TalonParameters.getInstance(parameters);
    talonParameters.configure(azimuth);
  }

  void setDriveParameters(String parameters) {
    TalonParameters talonParameters = TalonParameters.getInstance(parameters);
    talonParameters.configure(drive);
  }

  /**
   * Set the azimuth encoder relative to wheel zeroSensors position.
   *
   * @param zero encoder position (in ticks) where wheel is zeroed.
   */
  void setAzimuthZero(int zero) {
    double offset = getAzimuthAbsolutePosition() - zero; // encoder ticks
    double position = offset / 0xFFF; // wheel rotations
    azimuth.setPosition(position);
  }

  int getAzimuthAbsolutePosition() {
    return azimuth.getPulseWidthPosition() & 0xFFF;
  }

  CANTalon getAzimuth() {
    return azimuth;
  }

  CANTalon getDrive() {
    return drive;
  }
}
