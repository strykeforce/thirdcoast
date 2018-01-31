package org.strykeforce.thirdcoast.talon.control;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.Errors;

public class CurrentLimits {

  public static final CurrentLimits DEFAULT = new CurrentLimits(0, 0, 0);
  private static final Logger logger = LoggerFactory.getLogger(CurrentLimits.class);

  private final int continuous;
  private final int peak;
  private final int peakDuration;

  public CurrentLimits(int continuous, int peak, int peakDuration) {
    this.continuous = continuous;
    this.peak = peak;
    this.peakDuration = peakDuration;
  }

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
