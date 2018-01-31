package org.strykeforce.thirdcoast.talon.control;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.Errors;

@ParametersAreNonnullByDefault
public final class SoftLimits {

  public static final SoftLimits DEFAULT = new SoftLimits(State.DEFAULT, State.DEFAULT);
  private static final Logger logger = LoggerFactory.getLogger(SoftLimits.class);

  private final State forward, reverse;

  public SoftLimits(State forward, State reverse) {
    this.forward = forward;
    this.reverse = reverse;
  }

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
  public String toString() {
    return "SoftLimits{" + "forward=" + forward + ", reverse=" + reverse + '}';
  }

  static class State {
    static final State DEFAULT = new State(0, false);

    private final int limit;
    private final boolean enabled;

    public State(int limit, boolean enabled) {
      this.limit = limit;
      this.enabled = enabled;
    }

    @Override
    public String toString() {
      return "State{" + "limit=" + limit + ", enabled=" + enabled + '}';
    }
  }
}
