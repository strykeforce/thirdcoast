package org.strykeforce.thirdcoast.talon;

import com.ctre.CANTalon;
import com.ctre.CANTalon.VelocityMeasurementPeriod;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
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
  private final boolean isBrakeInNeutral;
  private final boolean isOutputReversed;
  private final VelocityMeasurementPeriod velocityMeasurementPeriod;
  private final int velocityMeasurementWindow;
  private final LimitSwitch forwardLimitSwitch;
  private final LimitSwitch reverseLimitSwitch;
  private final SoftLimit forwardSoftLimit;
  private final SoftLimit reverseSoftLimit;
  private final int currentLimit;

  TalonParameters(UnmodifiableConfig toml) {
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
   * Register a new configuration file containing Talon parameters. These parameter objects will be
   * merged with existing parameter objects. If a new parameter object has the same name as an
   * existing object, the old object will be overwritten.
   *
   * @param resourcePath path to TOML file in Jar archive
   */
  public static void register(String resourcePath) {

    List<Config> configList = readConfig(resourcePath).get("TALON");

    for (Config config : configList) {
      String name = config.get("name");
      if (name == null) {
        throw new IllegalArgumentException("TALON configuration name parameter missing");
      }

      String mode = (String) config.getOptional("mode").orElse("Voltage");
      TalonParameters talon = null;
      switch (CANTalon.TalonControlMode.valueOf(mode)) {
        case Voltage:
          talon = new VoltageTalonParameters(config);
          break;
        case Position:
          talon = new PositionTalonParameters(config);
          break;
        case Speed:
          talon = new SpeedTalonParameters(config);
          break;
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
   * Print the current state of the Talon.
   *
   * @param talon the Talon to print
   */
  public static void log(CANTalon talon) {
    System.out.println("talon = [" + talon + "]");
  }

  private static UnmodifiableConfig readConfig(String path) {
    try {
      URL configUrl = TalonParameters.class.getResource(path);
      if (configUrl == null) {
        throw new IllegalArgumentException("config not found: " + path);
      }
      File configFile = new File(configUrl.toURI());

      try (FileConfig config = FileConfig.of(configFile)) {
        config.load();
        System.out.println("config = " + config);
        return config.unmodifiable();
      }
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Configure a Talon with stored parameters.
   *
   * @param talon the Talon to configure
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
