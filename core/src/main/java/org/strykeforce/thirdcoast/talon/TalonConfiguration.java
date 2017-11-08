package org.strykeforce.thirdcoast.talon;

import com.ctre.CANTalon;
import com.ctre.CANTalon.VelocityMeasurementPeriod;
import edu.wpi.first.wpilibj.MotorSafety;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a Talon configuration.
 *
 * @see com.ctre.CANTalon
 */
public abstract class TalonConfiguration {

  final static Logger logger = LoggerFactory.getLogger(TalonConfiguration.class);

  // required
  private final String name;
  private final double setpointMax;

  // optional
  private final Encoder encoder;
  private final Boolean brakeInNeutral;
  private final Boolean outputReversed;
  private final VelocityMeasurementPeriod velocityMeasurementPeriod;
  private final Integer velocityMeasurementWindow;
  private final LimitSwitch forwardLimitSwitch;
  private final LimitSwitch reverseLimitSwitch;
  private final SoftLimit forwardSoftLimit;
  private final SoftLimit reverseSoftLimit;
  private final Integer currentLimit;

  public TalonConfiguration(String name, double setpointMax,
      Encoder encoder, Boolean brakeInNeutral, Boolean outputReversed,
      VelocityMeasurementPeriod velocityMeasurementPeriod, Integer velocityMeasurementWindow,
      LimitSwitch forwardLimitSwitch, LimitSwitch reverseLimitSwitch,
      SoftLimit forwardSoftLimit, SoftLimit reverseSoftLimit, Integer currentLimit) {
    this.name = name;
    this.setpointMax = setpointMax;
    this.encoder = encoder;
    this.brakeInNeutral = brakeInNeutral;
    this.outputReversed = outputReversed;
    this.velocityMeasurementPeriod = velocityMeasurementPeriod;
    this.velocityMeasurementWindow = velocityMeasurementWindow;
    this.forwardLimitSwitch = forwardLimitSwitch;
    this.reverseLimitSwitch = reverseLimitSwitch;
    this.forwardSoftLimit = forwardSoftLimit;
    this.reverseSoftLimit = reverseSoftLimit;
    this.currentLimit = currentLimit;
  }

  /**
   * Create a {@code TalonConfiguration} builder.
   *
   * @return the TalonConfigurationBuilder
   */
  public static TalonConfigurationBuilder builder() {
    return new TalonConfigurationBuilder();
  }

  /**
   * Print the current state of the Talon.
   *
   * @param talon the Talon to print
   */
  public static void log(CANTalon talon) {
    logger.info("TODO {}", talon);
  }

  /**
   * Configure a Talon with stored parameters.
   *
   * @param talon the Talon to registerWith
   */
  public void configure(CANTalon talon) {
    talon.setSafetyEnabled(false);
    talon.setProfile(0);
    talon.setExpiration(MotorSafety.DEFAULT_SAFETY_EXPIRATION);
    Encoder enc = encoder != null ? encoder : Encoder.DEFAULT;
    enc.configure(talon);

    talon.enableBrakeMode(brakeInNeutral != null ? brakeInNeutral : true);
    talon.reverseOutput(outputReversed != null ? outputReversed : false);

    if (velocityMeasurementPeriod != null) {
      talon.SetVelocityMeasurementPeriod(velocityMeasurementPeriod);
    } else {
      talon.SetVelocityMeasurementPeriod(VelocityMeasurementPeriod.Period_100Ms);
    }

    if (velocityMeasurementWindow != null) {
      talon.SetVelocityMeasurementWindow(velocityMeasurementWindow);
    } else {
      talon.SetVelocityMeasurementWindow(64);
    }

    LimitSwitch fls = forwardLimitSwitch != null ? forwardLimitSwitch : LimitSwitch.DEFAULT;
    LimitSwitch rls = reverseLimitSwitch != null ? reverseLimitSwitch : LimitSwitch.DEFAULT;
    talon.enableLimitSwitch(fls.isEnabled(), rls.isEnabled());
    if (fls.isEnabled()) {
      talon.ConfigFwdLimitSwitchNormallyOpen(fls.isNormallyOpen());
    }
    if (rls.isEnabled()) {
      talon.ConfigRevLimitSwitchNormallyOpen(rls.isNormallyOpen());
    }

    SoftLimit sl = forwardSoftLimit != null ? forwardSoftLimit : SoftLimit.DEFAULT;
    talon.enableForwardSoftLimit(sl.isEnabled());
    if (sl.isEnabled()) {
      talon.setForwardSoftLimit(sl.getPosition());
    }
    sl = reverseSoftLimit != null ? reverseSoftLimit : SoftLimit.DEFAULT;
    talon.enableReverseSoftLimit(sl.isEnabled());
    if (sl.isEnabled()) {
      talon.setReverseSoftLimit(sl.getPosition());
    }
    if (currentLimit != null && currentLimit > 0) {
      talon.setCurrentLimit(currentLimit);
      talon.EnableCurrentLimit(true);
    } else {
      talon.EnableCurrentLimit(false);
    }
  }

  /**
   * Get the name used to look up this Talon configuration.
   *
   * @return configuration name
   */
  public String getName() {
    return name;
  }

  public Encoder getEncoder() {
    return encoder;
  }

  public Boolean isBrakeInNeutral() {
    return brakeInNeutral;
  }

  public Boolean isOutputReversed() {
    return outputReversed;
  }

  public VelocityMeasurementPeriod getVelocityMeasurementPeriod() {
    return velocityMeasurementPeriod;
  }

  public Integer getVelocityMeasurementWindow() {
    return velocityMeasurementWindow;
  }

  public LimitSwitch getForwardLimitSwitch() {
    return forwardLimitSwitch;
  }

  public LimitSwitch getReverseLimitSwitch() {
    return reverseLimitSwitch;
  }

  public SoftLimit getForwardSoftLimit() {
    return forwardSoftLimit;
  }

  public SoftLimit getReverseSoftLimit() {
    return reverseSoftLimit;
  }

  /**
   * Get the current limit.
   *
   * @return the current limit, or null if not enabled.
   */
  public Integer getCurrentLimit() {
    return currentLimit;
  }

  /**
   * Maximum setpoint allowed by this Talon configuration. This is used by the {@link
   * org.strykeforce.thirdcoast.swerve.Wheel#set} to scale the drive output setpoint.
   *
   * @return the maximum allowed setpoint
   */
  public double getSetpointMax() {
    return setpointMax;
  }

  @Override
  public String toString() {
    return "TalonParameters{" +
        "name='" + name + '\'' +
        ", setpointMax=" + setpointMax +
        ", encoder=" + encoder +
        ", brakeInNeutral=" + brakeInNeutral +
        ", outputReversed=" + outputReversed +
        ", velocityMeasurementPeriod=" + velocityMeasurementPeriod +
        ", velocityMeasurementWindow=" + velocityMeasurementWindow +
        ", forwardLimitSwitch=" + forwardLimitSwitch +
        ", reverseLimitSwitch=" + reverseLimitSwitch +
        ", forwardSoftLimit=" + forwardSoftLimit +
        ", reverseSoftLimit=" + reverseSoftLimit +
        ", currentLimit=" + currentLimit +
        '}';
  }
}
