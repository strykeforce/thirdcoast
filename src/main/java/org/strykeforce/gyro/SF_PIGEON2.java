package org.strykeforce.gyro;

import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.Pigeon2Configuration;
import com.ctre.phoenix6.configs.Pigeon2Configurator;
import com.ctre.phoenix6.hardware.Pigeon2;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import org.strykeforce.telemetry.TelemetryService;

public class SF_PIGEON2 implements Gyro {
  private Pigeon2 pigeon2;
  private Pigeon2Configurator configurator;
  private StatusSignal<Angle> yawGetter;
  private StatusSignal<AngularVelocity> yawRateGetter;

  public SF_PIGEON2(int deviceId) {
    pigeon2 = new Pigeon2(deviceId);
    configurator = pigeon2.getConfigurator();
  }

  public SF_PIGEON2(int deviceId, String canbus) {
    pigeon2 = new Pigeon2(deviceId, canbus);
    configurator = pigeon2.getConfigurator();
    yawGetter = pigeon2.getYaw();
    yawRateGetter = pigeon2.getAngularVelocityZWorld();
  }

  @Override
  public void reset() {
    pigeon2.reset();
  }

  @Override
  public double getAngle() {
    return -yawGetter.refresh().getValueAsDouble();
  }

  @Override
  public double getRate() {
    return -yawRateGetter.refresh().getValueAsDouble();
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
    return pigeon2.getRoll().getValueAsDouble();
  }

  public Double getPitch() {
    return pigeon2.getPitch().getValueAsDouble();
  }

  public Double getYaw() {
    return pigeon2.getYaw().getValueAsDouble();
  }

  public void registerWith(TelemetryService telemetryService) {
    telemetryService.register(pigeon2);
  }
}
