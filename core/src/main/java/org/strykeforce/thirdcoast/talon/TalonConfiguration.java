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
  private final Boolean isBrakeInNeutral;
  private final Boolean isOutputReversed;
  private final VelocityMeasurementPeriod velocityMeasurementPeriod;
  private final Integer velocityMeasurementWindow;
  private final LimitSwitch forwardLimitSwitch;
  private final LimitSwitch reverseLimitSwitch;
  private final SoftLimit forwardSoftLimit;
  private final SoftLimit reverseSoftLimit;
  private final Integer currentLimit;

  public TalonConfiguration(String name, double setpointMax,
      Encoder encoder, Boolean isBrakeInNeutral, Boolean isOutputReversed,
      VelocityMeasurementPeriod velocityMeasurementPeriod, Integer velocityMeasurementWindow,
      LimitSwitch forwardLimitSwitch, LimitSwitch reverseLimitSwitch,
      SoftLimit forwardSoftLimit, SoftLimit reverseSoftLimit, Integer currentLimit) {
    this.name = name;
    this.setpointMax = setpointMax;
    this.encoder = encoder != null ? encoder : Encoder.DEFAULT;
    this.isBrakeInNeutral = isBrakeInNeutral;
    this.isOutputReversed = isOutputReversed;
    this.velocityMeasurementPeriod = velocityMeasurementPeriod;
    this.velocityMeasurementWindow = velocityMeasurementWindow;
    this.forwardLimitSwitch = forwardLimitSwitch != null ? forwardLimitSwitch : LimitSwitch.DEFAULT;
    this.reverseLimitSwitch = reverseLimitSwitch != null ? reverseLimitSwitch : LimitSwitch.DEFAULT;
    this.forwardSoftLimit = forwardSoftLimit != null ? forwardSoftLimit : SoftLimit.DEFAULT;
    this.reverseSoftLimit = reverseSoftLimit != null ? reverseSoftLimit : SoftLimit.DEFAULT;
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
    encoder.configure(talon);

    talon.enableBrakeMode(isBrakeInNeutral != null ? isBrakeInNeutral : true);
    talon.reverseOutput(isOutputReversed != null ? isOutputReversed : false);

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

    talon.enableLimitSwitch(forwardLimitSwitch.isEnabled(), reverseLimitSwitch.isEnabled());
    if (forwardLimitSwitch.isEnabled()) {
      talon.ConfigFwdLimitSwitchNormallyOpen(forwardLimitSwitch.isNormallyOpen());
    }
    if (reverseLimitSwitch.isEnabled()) {
      talon.ConfigRevLimitSwitchNormallyOpen(reverseLimitSwitch.isNormallyOpen());
    }
    talon.enableForwardSoftLimit(forwardSoftLimit.isEnabled());
    if (forwardSoftLimit.isEnabled()) {
      talon.setForwardSoftLimit(forwardSoftLimit.getValue());
    }
    talon.enableReverseSoftLimit(reverseSoftLimit.isEnabled());
    if (reverseSoftLimit.isEnabled()) {
      talon.setReverseSoftLimit(reverseSoftLimit.getValue());
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
    return isBrakeInNeutral;
  }

  public Boolean isOutputReversed() {
    return isOutputReversed;
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
        ", isBrakeInNeutral=" + isBrakeInNeutral +
        ", isOutputReversed=" + isOutputReversed +
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
