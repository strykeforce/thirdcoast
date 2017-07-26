package org.strykeforce.sidewinder.talon;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.VelocityMeasurementPeriod;
import com.electronwill.nightconfig.toml.TomlConfig;
import com.electronwill.nightconfig.toml.TomlParser;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configure Talon SRX parameters. This class will read CANTalon configurations located in TOML
 * configuration files stored on the classpath, typically in the robot's JAR file.
 *
 * <p>Multiple configuration files can be registered by calling the {@code register} method.
 *
 * @see com.ctre.CANTalon
 */
public abstract class TalonParameters {

  private static final Map<String, TalonParameters> settings = new ConcurrentHashMap<>();
  // required
  private final String name;
  private final double setpointMax;
  // optional
  private final Encoder encoder;
  //  private final CANTalon.FeedbackDevice feedbackDevice;
  private final boolean isBrakeInNeutral;
  //  private final boolean isEncoderReversed;
  private final boolean isOutputReversed;
  private final VelocityMeasurementPeriod velocityMeasurementPeriod;
  private final int velocityMeasurementWindow;
  private final LimitSwitch forwardLimitSwitch;
  private final LimitSwitch reverseLimitSwitch;
  private final SoftLimit forwardSoftLimit;
  private final SoftLimit reverseSoftLimit;
  private final int currentLimit;

  TalonParameters(TomlConfig toml) {
    name = toml.getValue("name");
    try {
      setpointMax = toml.getValue("setpoint_max");
    } catch (NullPointerException e) {
      throw new IllegalArgumentException("TALON setpoint_max parameter missing in: " + name);
    }

    encoder = new Encoder(toml.getOptionalValue("feedback_device"),
        toml.getOptionalValue("encoder_reversed"),
        toml.getOptionalValue("ticks_per_revolution"));

//    feedbackDevice = CANTalon.FeedbackDevice.valueOf(
//        (String) toml.getOptionalValue("feedback_device").orElse("QuadEncoder"));

    isBrakeInNeutral = (boolean) toml.getOptionalValue("brake_in_neutral").orElse(true);

//    isEncoderReversed = (boolean) toml.getOptionalValue("encoder_reversed").orElse(false);
    isOutputReversed = (boolean) toml.getOptionalValue("output_reversed").orElse(false);

    int vmp = (int) toml.getOptionalValue("velocity_measurement_period").orElse(100);
    velocityMeasurementPeriod = VelocityMeasurementPeriod.valueOf(vmp);
    if (velocityMeasurementPeriod == null) {
      throw new IllegalArgumentException("TALON velocity_measurement_period invalid: " + vmp);
    }
    velocityMeasurementWindow = (int) toml.getOptionalValue("velocity_measurement_window")
        .orElse(64);

    forwardLimitSwitch = new LimitSwitch(toml.getOptionalValue("forward_limit_switch"));
    reverseLimitSwitch = new LimitSwitch(toml.getOptionalValue("reverse_limit_switch"));

    forwardSoftLimit = new SoftLimit(toml.getOptionalValue("forward_soft_limit"));
    reverseSoftLimit = new SoftLimit(toml.getOptionalValue("reverse_soft_limit"));

    currentLimit = (int) toml.getOptionalValue("current_limit").orElse(0);
  }

  /**
   * Register a new configuration file containing Talon parameters. These parameter objects will be
   * merged with existing parameter objects. If a new parameter object has the same name as an
   * existing object, the old object will be overwritten.
   *
   * @param resourcePath path to TOML file in Jar archive
   */
  public static void register(String resourcePath) {
    InputStream is = TalonParameters.class.getResourceAsStream(resourcePath);
    if (is == null) {
      throw new IllegalArgumentException("No talon parameter resource at " + resourcePath);
    }

    TomlConfig config = new TomlParser().parse(is);

    List<TomlConfig> talons = config.getValue("TALON");

    for (TomlConfig toml : talons) {
      String name = toml.getValue("name");
      if (name == null) {
        throw new IllegalArgumentException("TALON configuration name parameter missing");
      }

      String mode = (String) toml.getOptionalValue("mode").orElse("Voltage");
      TalonParameters talon = null;
      switch (CANTalon.TalonControlMode.valueOf(mode)) {
        case Voltage:
          talon = new VoltageTalonParameters(toml);
          break;
        case Position:
        case Speed:
        case Follower:
        case MotionMagic:
        case Current:
        case MotionProfile:
        case Disabled:
        case PercentVbus:
          throw new IllegalStateException("talon mode not implemented: " + mode);
      }
      settings.put(name, talon);
    }
  }

  /**
   * Return Talon parameters for the named configuration.
   *
   * @param name the name of the Talon set of parameters
   * @return configured Talon parameter immutable object
   */
  public static TalonParameters getInstance(String name) {
    return settings.get(name);
  }

  /**
   * Configure a Talon with stored parameters.
   *
   * @param talon the Talon to configure
   */
  public void configure(CANTalon talon) {
    encoder.configure(talon);
//    talon.setFeedbackDevice(encoder.getFeedbackDevice());
//    talon.reverseSensor(encoder.isReversed());
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

  public String getName() {
    return name;
  }

  public Encoder getEncoder() {
    return encoder;
  }

  public boolean isBrakeInNeutral() {
    return isBrakeInNeutral;
  }

  public boolean isOutputReversed() {
    return isOutputReversed;
  }

  public VelocityMeasurementPeriod getVelocityMeasurementPeriod() {
    return velocityMeasurementPeriod;
  }

  public int getVelocityMeasurementWindow() {
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

  public int getCurrentLimit() {
    return currentLimit;
  }

  public double getSetpointMax() {
    return setpointMax;
  }

  @Override
  public String toString() {
    return String.format("TalonParameters = %s", name);
  }

}
