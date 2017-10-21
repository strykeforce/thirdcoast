package org.strykeforce.thirdcoast.talon;

import com.ctre.CANTalon;
import com.ctre.CANTalon.VelocityMeasurementPeriod;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a Talon configuration.
 *
 * @see com.ctre.CANTalon
 */
public abstract class TalonConfiguration {

  private static final Map<String, TalonConfiguration> settings = new ConcurrentHashMap<>();
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
    name = toml.get("name");
    try {
      setpointMax = toml.get("setpoint_max");
    } catch (NullPointerException e) {
      throw new IllegalArgumentException("TALON setpoint_max parameter missing in: " + name);
    }

    encoder = new Encoder(toml.getOptional("feedback_device"),
        toml.getOptional("encoder_reversed"),
        toml.getOptional("ticks_per_revolution"));

    isBrakeInNeutral = (boolean) toml.getOptional("brake_in_neutral").orElse(true);
    isOutputReversed = (boolean) toml.getOptional("output_reversed").orElse(false);

    int vmp = (int) toml.getOptional("velocity_measurement_period").orElse(100);
    velocityMeasurementPeriod = VelocityMeasurementPeriod.valueOf(vmp);
    if (velocityMeasurementPeriod == null) {
      throw new IllegalArgumentException("TALON velocity_measurement_period invalid: " + vmp);
    }
    velocityMeasurementWindow = (int) toml.getOptional("velocity_measurement_window")
        .orElse(64);

    forwardLimitSwitch = new LimitSwitch(toml.getOptional("forward_limit_switch"));
    reverseLimitSwitch = new LimitSwitch(toml.getOptional("reverse_limit_switch"));

    forwardSoftLimit = new SoftLimit(toml.getOptional("forward_soft_limit"));
    reverseSoftLimit = new SoftLimit(toml.getOptional("reverse_soft_limit"));

    currentLimit = (int) toml.getOptional("current_limit").orElse(0);
  }


  /**
   * Print the current state of the Talon.
   *
   * @param talon the Talon to print
   */
  public static void log(CANTalon talon) {
    System.out.println("talon = [" + talon + "]");
  }
  
  /**
   * Configure a Talon with stored parameters.
   *
   * @param talon the Talon to registerWith
   */
  public void configure(CANTalon talon) {
    talon.setSafetyEnabled(false);
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
