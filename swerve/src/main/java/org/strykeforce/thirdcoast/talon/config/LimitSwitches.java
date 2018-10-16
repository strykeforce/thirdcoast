package org.strykeforce.thirdcoast.talon.config;

import static com.ctre.phoenix.motorcontrol.LimitSwitchNormal.Disabled;
import static com.ctre.phoenix.motorcontrol.LimitSwitchSource.Deactivated;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
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
public class LimitSwitches implements Configurable {

  public static final LimitSwitches DEFAULT = new LimitSwitches(State.DEFAULT, State.DEFAULT);
  private static final Logger logger = LoggerFactory.getLogger(LimitSwitches.class);

  private final State forward, reverse;

  public LimitSwitches(State forward, State reverse) {
    this.forward = forward;
    this.reverse = reverse;
  }

  public static LimitSwitches create(@Nullable Toml toml) {
    if (toml == null) {
      return DEFAULT;
    }
    return new LimitSwitches(
        State.create(toml.getTable("forward")), State.create(toml.getTable("reverse")));
  }

  @Override
  public void configure(TalonSRX talon, int timeout) {
    ErrorCode err = talon.configForwardLimitSwitchSource(forward.source, forward.normal, timeout);
    Errors.check(talon, "configForwardLimitSwitchSource", err, logger);
    err = talon.configReverseLimitSwitchSource(reverse.source, reverse.normal, timeout);
    Errors.check(talon, "configReverseLimitSwitchSource", err, logger);

    boolean enabled = forward.normal != Disabled || reverse.normal != Disabled;
    talon.overrideLimitSwitchesEnable(enabled);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LimitSwitches that = (LimitSwitches) o;
    return Objects.equals(forward, that.forward) && Objects.equals(reverse, that.reverse);
  }

  @Override
  public int hashCode() {
    return Objects.hash(forward, reverse);
  }

  @Override
  public String toString() {
    return "LimitSwitches{" + "forward=" + forward + ", reverse=" + reverse + '}';
  }

  static class State {
    static final State DEFAULT = new State(Deactivated, Disabled);
    private static final Toml DEFAULT_TOML = new Toml().read(new TomlWriter().write(State.DEFAULT));

    private final LimitSwitchSource source;
    private final LimitSwitchNormal normal;

    public State(LimitSwitchSource source, LimitSwitchNormal normal) {
      this.source = source;
      this.normal = normal;
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
      return source == state.source && normal == state.normal;
    }

    @Override
    public int hashCode() {
      return Objects.hash(source, normal);
    }

    @Override
    public String toString() {
      return "State{" + "source=" + source + ", normal=" + normal + '}';
    }
  }
}
