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
public class SoftLimits implements Configurable {

  public static final SoftLimits DEFAULT = new SoftLimits(State.DEFAULT, State.DEFAULT);
  private static final Logger logger = LoggerFactory.getLogger(SoftLimits.class);

  private final State forward, reverse;

  public SoftLimits(State forward, State reverse) {
    this.forward = forward;
    this.reverse = reverse;
  }

  public static SoftLimits create(Toml toml) {
    if (toml == null) {
      return DEFAULT;
    }
    return new SoftLimits(
        State.create(toml.getTable("forward")), State.create(toml.getTable("reverse")));
  }

  @Override
  public void configure(TalonSRX talon, int timeout) {
    ErrorCode err = talon.configForwardSoftLimitThreshold(forward.limit, timeout);
    Errors.check(talon, "configForwardSoftLimitThreshold", err, logger);
    err = talon.configForwardSoftLimitEnable(forward.enabled, timeout);
    Errors.check(talon, "configForwardSoftLimitEnable", err, logger);

    err = talon.configReverseSoftLimitThreshold(reverse.limit, timeout);
    Errors.check(talon, "configReverseSoftLimitThreshold", err, logger);
    err = talon.configReverseSoftLimitEnable(reverse.enabled, timeout);
    Errors.check(talon, "configReverseSoftLimitEnable", err, logger);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SoftLimits that = (SoftLimits) o;
    return Objects.equals(forward, that.forward) && Objects.equals(reverse, that.reverse);
  }

  @Override
  public int hashCode() {
    return Objects.hash(forward, reverse);
  }

  @Override
  public String toString() {
    return "SoftLimits{" + "forward=" + forward + ", reverse=" + reverse + '}';
  }

  static class State {
    static final State DEFAULT = new State(0, false);
    private static final Toml DEFAULT_TOML = new Toml().read(new TomlWriter().write(State.DEFAULT));

    private final int limit;
    private final boolean enabled;

    public State(int limit, boolean enabled) {
      this.limit = limit;
      this.enabled = enabled;
    }

    public static State create(@Nullable Toml toml) {
      if (toml == null) {
        return DEFAULT;
      }
      return new Toml(DEFAULT_TOML).read(toml).to(State.class);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      State state = (State) o;
      return limit == state.limit && enabled == state.enabled;
    }

    @Override
    public int hashCode() {
      return Objects.hash(limit, enabled);
    }

    @Override
    public String toString() {
      return "State{" + "limit=" + limit + ", enabled=" + enabled + '}';
    }
  }
}
