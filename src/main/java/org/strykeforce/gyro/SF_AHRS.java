package org.strykeforce.gyro;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SerialPort;

public class SF_AHRS extends AHRS implements Gyro {
  private AHRS ahrs;

  public SF_AHRS(SPI.Port spi_port_id, byte update_rate_hz) {
    ahrs = new AHRS(spi_port_id, update_rate_hz);
  }

  public SF_AHRS(SPI.Port spi_port_id, int spi_bitrate, byte update_rate_hz) {
    ahrs = new SF_AHRS(spi_port_id, spi_bitrate, update_rate_hz);
  }

  public SF_AHRS(I2C.Port i2c_port_id, byte update_rate_hz) {
    ahrs = new AHRS(i2c_port_id, update_rate_hz);
  }

  public SF_AHRS(SerialPort.Port serial_port_id, SerialDataType data_type, byte update_rate_hz) {
    ahrs = new AHRS(serial_port_id, data_type, update_rate_hz);
  }

  public SF_AHRS(SPI.Port spi_port_id) {
    ahrs = new AHRS(spi_port_id);
  }

  public SF_AHRS(I2C.Port i2c_port_id) {
    ahrs = new AHRS(i2c_port_id);
  }

  public SF_AHRS(SerialPort.Port serial_port_id) {
    ahrs = new AHRS(serial_port_id);
  }

  public SF_AHRS() {
    ahrs = new AHRS();
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
}
