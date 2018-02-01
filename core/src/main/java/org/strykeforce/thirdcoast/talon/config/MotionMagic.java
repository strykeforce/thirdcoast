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
public class MotionMagic {

  public static final MotionMagic DEFAULT = new MotionMagic(0, 0);
  private static final Toml DEFAULT_TOML =
      new Toml().read(new TomlWriter().write(MotionMagic.DEFAULT));

  private static final Logger logger = LoggerFactory.getLogger(MotionMagic.class);

  private final int acceleration;
  private final int cruiseVelocity;

  public MotionMagic(int acceleration, int cruiseVelocity) {
    this.acceleration = acceleration;
    this.cruiseVelocity = cruiseVelocity;
  }

  public static MotionMagic create(@Nullable Toml toml) {
    if (toml == null) {
      return DEFAULT;
    }
    return new Toml(DEFAULT_TOML).read(toml).to(MotionMagic.class);
  }

  public void configure(TalonSRX talon, int timeout) {
    ErrorCode err = talon.configMotionAcceleration(acceleration, timeout);
    Errors.check(talon, "configMotionAcceleration", err, logger);
    err = talon.configMotionCruiseVelocity(cruiseVelocity, timeout);
    Errors.check(talon, "configMotionCruiseVelocity", err, logger);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MotionMagic that = (MotionMagic) o;
    return acceleration == that.acceleration && cruiseVelocity == that.cruiseVelocity;
  }

  @Override
  public int hashCode() {
    return Objects.hash(acceleration, cruiseVelocity);
  }

  @Override
  public String toString() {
    return "MotionMagic{"
        + "acceleration="
        + acceleration
        + ", cruiseVelocity="
        + cruiseVelocity
        + '}';
  }
}
