package org.strykeforce.gyro;

import com.ctre.phoenix6.configs.Pigeon2Configuration;
import com.ctre.phoenix6.configs.Pigeon2Configurator;
import com.ctre.phoenix6.hardware.Pigeon2;
import edu.wpi.first.math.geometry.Rotation2d;
import org.strykeforce.telemetry.TelemetryService;

public class SF_PIGEON2 implements Gyro {
  private Pigeon2 pigeon2;
  private Pigeon2Configurator configurator;

  public SF_PIGEON2(int deviceId) {
    pigeon2 = new Pigeon2(deviceId);
    configurator = pigeon2.getConfigurator();
  }

  public SF_PIGEON2(int deviceId, String canbus) {
    pigeon2 = new Pigeon2(deviceId, canbus);
    configurator = pigeon2.getConfigurator();
  }

  @Override
  public void reset() {
    pigeon2.reset();
  }

  @Override
  public double getAngle() {
    return pigeon2.getAngle();
  }

  @Override
  public double getRate() {
    return pigeon2.getRate();
  }

  @Override
  public Rotation2d getRotation2d() {
    return pigeon2.getRotation2d();
  }

  public Pigeon2 getPigeon2() {
    return pigeon2;
  }

  public void applyConfig(Pigeon2Configuration config) {
    configurator.apply(config);
  }

  public Double getRoll() {
    return pigeon2.getRoll().getValue();
  }

  public Double getPitch() {
    return pigeon2.getPitch().getValue();
  }

  public Double getYaw() {
    return pigeon2.getYaw().getValue();
  }

  public void registerWith(TelemetryService telemetryService) {
    telemetryService.register(pigeon2);
  }
}
