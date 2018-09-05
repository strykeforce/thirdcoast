package org.strykeforce.thirdcoast.talon.config;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.Errors;

@ParametersAreNonnullByDefault
public class VelocityMeasurement implements Configurable {

  public static final VelocityMeasurement DEFAULT =
      new VelocityMeasurement(VelocityMeasPeriod.Period_100Ms, 64);
  private static final Toml DEFAULT_TOML =
      new Toml().read(new TomlWriter().write(VelocityMeasurement.DEFAULT));
  private static final Logger logger = LoggerFactory.getLogger(VelocityMeasurement.class);

  private final VelocityMeasPeriod period;
  private final int window;

  public VelocityMeasurement(VelocityMeasPeriod period, int window) {
    this.period = period;
    this.window = window;
  }

  public static VelocityMeasurement create(@Nullable Toml toml) {
    if (toml == null) {
      return DEFAULT;
    }
    return new Toml(DEFAULT_TOML).read(toml).to(VelocityMeasurement.class);
  }

  @Override
  public void configure(TalonSRX talon, int timeout) {
    ErrorCode err = talon.configVelocityMeasurementPeriod(period, timeout);
    Errors.check(talon, "configVelocityMeasurementPeriod", err, logger);
    err = talon.configVelocityMeasurementWindow(window, timeout);
    Errors.check(talon, "configVelocityMeasurementWindow", err, logger);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VelocityMeasurement that = (VelocityMeasurement) o;
    return window == that.window && period == that.period;
  }

  @Override
  public int hashCode() {
    return Objects.hash(period, window);
  }

  @Override
  public String toString() {
    return "VelocityMeasurement{" + "period=" + period + ", window=" + window + '}';
  }
}
