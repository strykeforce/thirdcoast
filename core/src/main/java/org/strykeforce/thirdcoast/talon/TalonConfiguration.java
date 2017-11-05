package org.strykeforce.thirdcoast.talon;

import com.ctre.CANTalon;
import com.ctre.CANTalon.VelocityMeasurementPeriod;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import edu.wpi.first.wpilibj.MotorSafety;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a Talon configuration.
 *
 * @see com.ctre.CANTalon
 */
public abstract class TalonConfiguration {

  public final static String NAME = "name";
  public final static String MODE = "mode";
  public final static String SETPOINT_MAX = "setpoint_max";
  public final static String FEEDBACK_DEVICE = "feedback_device";
  public final static String ENCODER_REVERSED = "encoder_reversed";
  public final static String TICKS_PER_REVOLUTION = "ticks_per_revolution";
  public final static String BRAKE_IN_NEUTRAL = "brake_in_neutral";
  public final static String OUTPUT_REVERSED = "output_reversed";
  public final static String VELOCITY_MEASUREMENT_PERIOD = "velocity_measurement_period";
  public final static String VELOCITY_MEASUREMENT_WINDOW = "velocity_measurement_window";
  public final static String FORWARD_LIMIT_SWITCH = "forward_limit_switch";
  public final static String REVERSE_LIMIT_SWITCH = "reverse_limit_switch";
  public final static String FORWARD_SOFT_LIMIT = "forward_soft_limit";
  public final static String REVERSE_SOFT_LIMIT = "reverse_soft_limit";
  public final static String CURRENT_LIMIT = "current_limit";
  final static Logger logger = LoggerFactory.getLogger(TalonConfiguration.class);
  
  // required
  private final String name;
  private final double setpointMax;

  // optional
  private final Encoder encoder;
  private final boolean isBrakeInNeutral;
  private final boolean isOutputReversed;
  private final VelocityMeasurementPeriod velocityMeasurementPeriod;
  private final int velocityMeasurementWindow;
  private final LimitSwitch forwardLimitSwitch;
  private final LimitSwitch reverseLimitSwitch;
  private final SoftLimit forwardSoftLimit;
  private final SoftLimit reverseSoftLimit;
  private final int currentLimit;

  TalonConfiguration(UnmodifiableConfig toml) {
    name = toml.get(NAME);
    if (name == null) {
      throw new IllegalArgumentException("TALON configuration name parameter missing");
    }
    Double odouble = toml.get(SETPOINT_MAX);
    if (odouble == null) {
      throw new IllegalArgumentException(
          String.format("TALON %s missing for %s", SETPOINT_MAX, name));
    }
    setpointMax = odouble;

    encoder = new Encoder(toml.getOptional(FEEDBACK_DEVICE), toml.getOptional(ENCODER_REVERSED),
        toml.getOptional(TICKS_PER_REVOLUTION));

    isBrakeInNeutral = (boolean) toml.getOptional(BRAKE_IN_NEUTRAL).orElse(true);
    isOutputReversed = (boolean) toml.getOptional(OUTPUT_REVERSED).orElse(false);

    int vmp = (int) toml.getOptional(VELOCITY_MEASUREMENT_PERIOD).orElse(100);
    velocityMeasurementPeriod = VelocityMeasurementPeriod.valueOf(vmp);
    if (velocityMeasurementPeriod == null) {
      throw new IllegalArgumentException(
          "TALON " + VELOCITY_MEASUREMENT_PERIOD + " invalid: " + vmp);
    }
    velocityMeasurementWindow = (int) toml.getOptional(VELOCITY_MEASUREMENT_WINDOW).orElse(64);

    forwardLimitSwitch = new LimitSwitch(toml.getOptional(FORWARD_LIMIT_SWITCH));
    reverseLimitSwitch = new LimitSwitch(toml.getOptional(REVERSE_LIMIT_SWITCH));

    forwardSoftLimit = new SoftLimit(toml.getOptional(FORWARD_SOFT_LIMIT));
    reverseSoftLimit = new SoftLimit(toml.getOptional(REVERSE_SOFT_LIMIT));

    currentLimit = (int) toml.getOptional(CURRENT_LIMIT).orElse(0);
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
    talon.enableBrakeMode(isBrakeInNeutral);
    talon.reverseOutput(isOutputReversed);
    talon.SetVelocityMeasurementPeriod(velocityMeasurementPeriod);
    talon.SetVelocityMeasurementWindow(velocityMeasurementWindow);
    talon.enableLimitSwitch(forwardLimitSwitch.isEnabled(), reverseLimitSwitch.isEnabled());
    if (forwardLimitSwitch.isEnabled()) {
      talon.ConfigFwdLimitSwitchNormallyOpen(forwardLimitSwitch.isNormallyOpen());
    }
    if (reverseLimitSwitch.isEnabled()) {
      talon.ConfigRevLimitSwitchNormallyOpen(reverseLimitSwitch.isNormallyOpen());
    }
    if (forwardSoftLimit.isEnabled()) {
      talon.enableForwardSoftLimit(true);
      talon.setForwardSoftLimit(forwardSoftLimit.getValue());
    }
    if (reverseSoftLimit.isEnabled()) {
      talon.enableReverseSoftLimit(true);
      talon.setReverseSoftLimit(reverseSoftLimit.getValue());
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

  Encoder getEncoder() {
    return encoder;
  }

  boolean isBrakeInNeutral() {
    return isBrakeInNeutral;
  }

  boolean isOutputReversed() {
    return isOutputReversed;
  }

  VelocityMeasurementPeriod getVelocityMeasurementPeriod() {
    return velocityMeasurementPeriod;
  }

  int getVelocityMeasurementWindow() {
    return velocityMeasurementWindow;
  }

  LimitSwitch getForwardLimitSwitch() {
    return forwardLimitSwitch;
  }

  LimitSwitch getReverseLimitSwitch() {
    return reverseLimitSwitch;
  }

  SoftLimit getForwardSoftLimit() {
    return forwardSoftLimit;
  }

  SoftLimit getReverseSoftLimit() {
    return reverseSoftLimit;
  }

  int getCurrentLimit() {
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
