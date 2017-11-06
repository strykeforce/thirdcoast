package org.strykeforce.thirdcoast.talon;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.InMemoryFormat;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  public final static UnmodifiableConfig DEFAULT; // FIXME: = TalonConfigBuilder.DEFAULT
  public final static String TALON_TABLE = "TALON";
  public final static String DEFAULT_CONFIG = "voltage.default";
  final static Logger logger = LoggerFactory.getLogger(TalonProvisioner.class);

  static {
    Config c = Config.inMemory();
    c.add(Arrays.asList(TalonConfigurationBuilder.NAME), DEFAULT_CONFIG);
    c.add(Arrays.asList(TalonConfigurationBuilder.MODE), TalonControlMode.Voltage.name());
    c.add(Arrays.asList(TalonConfigurationBuilder.SETPOINT_MAX), 12.0);
    Config d = InMemoryFormat.defaultInstance().createConfig();
    d.add(TALON_TABLE, Arrays.asList(c));
    DEFAULT = d.unmodifiable();
  }

  private final Map<String, TalonConfiguration> settings = new HashMap<>();

  /**
   * Construct the TalonProvisioner with base talon configurations that include swerve drive
   * motors.
   *
   * @param configs base configuration that should include swerve azimuth and drive configs
   */
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
    List<Config> configList = configs.get(TALON_TABLE);
    if (configList == null) {
      logger.warn("no " + TALON_TABLE + " tables in config");
      return;
    }

    for (UnmodifiableConfig config : configList) {
      String name = config.get(TalonConfigurationBuilder.NAME);
      if (name == null) {
        throw new IllegalArgumentException(TALON_TABLE + " configuration name parameter missing");
      }

      TalonConfigurationBuilder builder = new TalonConfigurationBuilder(config);
      settings.put(name, builder.build());
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

  @Override
  public String toString() {
    return "TalonProvisioner{" +
        "settings=" + settings +
        '}';
  }
}
