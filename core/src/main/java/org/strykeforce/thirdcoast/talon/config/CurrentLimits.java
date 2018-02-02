package org.strykeforce.thirdcoast.talon.config;

import com.ctre.phoenix.ErrorCode;
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
public class CurrentLimits implements Configurable {

  public static final CurrentLimits DEFAULT = new CurrentLimits(0, 0, 0);
  private static final Toml DEFAULT_TOML =
      new Toml().read(new TomlWriter().write(CurrentLimits.DEFAULT));

  private static final Logger logger = LoggerFactory.getLogger(CurrentLimits.class);

  private final int continuous;
  private final int peak;
  private final int peakDuration;

  public CurrentLimits(int continuous, int peak, int peakDuration) {
    this.continuous = continuous;
    this.peak = peak;
    this.peakDuration = peakDuration;
  }

  public static CurrentLimits create(@Nullable Toml toml) {
    if (toml == null) {
      return DEFAULT;
    }
    return new Toml(DEFAULT_TOML).read(toml).to(CurrentLimits.class);
  }

  @Override
  public void configure(TalonSRX talon, int timeout) {
    ErrorCode err;
    err = talon.configContinuousCurrentLimit(continuous, timeout);
    Errors.check(talon, "configContinuousCurrentLimit", err, logger);

    err = talon.configPeakCurrentLimit(peak, timeout);
    Errors.check(talon, "configPeakCurrentLimit", err, logger);

    err = talon.configPeakCurrentDuration(peakDuration, timeout);
    Errors.check(talon, "configPeakCurrentDuration", err, logger);

    talon.enableCurrentLimit(continuous > 0 || peak > 0);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CurrentLimits that = (CurrentLimits) o;
    return continuous == that.continuous && peak == that.peak && peakDuration == that.peakDuration;
  }

  @Override
  public int hashCode() {
    return Objects.hash(continuous, peak, peakDuration);
  }

  @Override
  public String toString() {
    return "CurrentLimits{"
        + "continuous="
        + continuous
        + ", peak="
        + peak
        + ", peakDuration="
        + peakDuration
        + '}';
  }
}
