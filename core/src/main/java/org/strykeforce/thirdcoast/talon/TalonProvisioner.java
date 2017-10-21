package org.strykeforce.thirdcoast.talon;

import com.ctre.CANTalon;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * A service for provisioning Talons. This class will read CANTalon configurations located in TOML
 * configuration objects registered using the {@link #addConfigurations(UnmodifiableConfig)} class
 * method.
 *
 * <p>Multiple configurations can be registered by calling the {@code register} method repeatedly.
 *
 * @see com.ctre.CANTalon
 */
@Singleton
public class TalonProvisioner {

  private final Map<String, TalonConfiguration> settings = new HashMap<>();

  @Inject
  public TalonProvisioner(UnmodifiableConfig configs) {
    addConfigurations(configs);
  }

  /**
   * Register a new configuration file containing Talon parameters. These parameter objects will be
   * merged with existing parameter objects. If a new parameter object has the same name as an
   * existing object, the old object will be overwritten.
   *
   * @param configs a parsed config collection
   */
  public void addConfigurations(UnmodifiableConfig configs) {
    List<Config> configList = configs.get("TALON");
    if (configList == null) {
      throw new IllegalArgumentException("no TALONS in config");
    }

    for (UnmodifiableConfig config : configList) {
      String name = config.get("name");
      if (name == null) {
        throw new IllegalArgumentException("TALON configuration name parameter missing");
      }

      String mode = (String) config.getOptional("mode").orElse("Voltage");
      TalonConfiguration talon = null;
      switch (CANTalon.TalonControlMode.valueOf(mode)) {
        case Voltage:
          talon = new VoltageTalonConfiguration(config);
          break;
        case Position:
          talon = new PositionTalonConfiguration(config);
          break;
        case Speed:
          talon = new SpeedTalonConfiguration(config);
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
  public TalonConfiguration configurationFor(String name) {
    TalonConfiguration config = settings.get(name);
    if (config == null) {
      throw new IllegalArgumentException("Talon configuration not found: " + name);
    }
    return config;
  }


}
