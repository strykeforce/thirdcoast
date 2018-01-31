package org.strykeforce.thirdcoast.talon.control;

import static com.ctre.phoenix.motorcontrol.LimitSwitchNormal.Disabled;
import static com.ctre.phoenix.motorcontrol.LimitSwitchSource.Deactivated;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.Errors;

@ParametersAreNonnullByDefault
public final class LimitSwitches {

  public static final LimitSwitches DEFAULT = new LimitSwitches(State.DEFAULT, State.DEFAULT);
  private static final Logger logger = LoggerFactory.getLogger(LimitSwitches.class);

  private final State forward, reverse;

  public LimitSwitches(State forward, State reverse) {
    this.forward = forward;
    this.reverse = reverse;
  }

  public void configure(TalonSRX talon, int timeout) {
    ErrorCode err = talon.configForwardLimitSwitchSource(forward.source, forward.normal, timeout);
    Errors.check(talon, "configForwardLimitSwitchSource", err, logger);
    err = talon.configReverseLimitSwitchSource(reverse.source, reverse.normal, timeout);
    Errors.check(talon, "configReverseLimitSwitchSource", err, logger);

    boolean enabled = forward.normal != Disabled || reverse.normal != Disabled;
    talon.overrideLimitSwitchesEnable(enabled);
  }

  @Override
  public String toString() {
    return "LimitSwitches{" + "forward=" + forward + ", reverse=" + reverse + '}';
  }

  static class State {
    static final State DEFAULT = new State(Deactivated, Disabled);

    private final LimitSwitchSource source;
    private final LimitSwitchNormal normal;

    public State(LimitSwitchSource source, LimitSwitchNormal normal) {
      this.source = source;
      this.normal = normal;
    }

    @Override
    public String toString() {
      return "State{" + "source=" + source + ", normal=" + normal + '}';
    }
  }
}
