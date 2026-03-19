package org.strykeforce.gyro;

// import com.kauailabs.navx.frc.AHRS;
import com.studica.frc.AHRS;
import com.studica.frc.AHRS.NavXComType;
import com.studica.frc.AHRS.NavXUpdateRate;
import edu.wpi.first.math.geometry.Rotation2d;

public class SF_AHRS implements Gyro {
  private AHRS ahrs;

  public SF_AHRS(NavXComType comType) {
    ahrs = new AHRS(comType);
  }

  public SF_AHRS(NavXComType comType, NavXUpdateRate updateRate) {
    ahrs = new AHRS(comType, updateRate);
  }

  public SF_AHRS(NavXComType comType, int customRateHz) {
    ahrs = new AHRS(comType, customRateHz);
  }

  public SF_AHRS() {
    ahrs = new AHRS(NavXComType.kMXP_SPI);
  }

  @Override
  public void reset() {
    ahrs.reset();
  }

  @Override
  public double getAngle() {
    return ahrs.getAngle();
  }

  @Override
  public double getRate() {
    return ahrs.getRate();
  }

  @Override
  public Rotation2d getRotation2d() {
    return ahrs.getRotation2d();
  }

  // Returns roll in degrees
  public double getRoll() {
    return ahrs.getRoll();
  }

  // Returns pitch in degrees
  public double getPitch() {
    return ahrs.getPitch();
  }

  // Returns x-axis acceleration
  public double getAccelerationX() {
    return ahrs.getWorldLinearAccelX();
  }

  // Returns y-axis acceleration
  public double getAccelerationY() {
    return ahrs.getWorldLinearAccelY();
  }

  // Returns the z-axis acceleration
  public double getAccelerationZ() {
    return ahrs.getWorldLinearAccelZ();
  }
}
