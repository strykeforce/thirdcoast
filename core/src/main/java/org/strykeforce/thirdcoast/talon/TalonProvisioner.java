package org.strykeforce.thirdcoast.talon;

import com.moandjiezana.toml.Toml;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.util.Settings;

/**
 * A service for provisioning Talons. This class will read TalonSRX configurations located in TOML
 * configuration objects registered using the {@link #addConfigurations(Settings)} class method.
 *
 * <p>Multiple configurations can be registered by calling the {@code register} method repeatedly.
 *
 * @see com.ctre.phoenix.motorcontrol.can.TalonSRX
 */
@Singleton
@ParametersAreNonnullByDefault
public class TalonProvisioner {

  public static final String TALON_TABLE = "TALON";
  static final String TABLE = "THIRDCOAST.TALON";
  private static final Logger logger = LoggerFactory.getLogger(TalonProvisioner.class);
  private final int timeout;
  @NotNull private final Map<String, TalonConfiguration> configs = new HashMap<>();

  /**
   * Construct the TalonProvisioner with base talon configurations that include swerve drive motors.
   *
   * @param settings base configuration that should include swerve azimuth and drive configs
   * @throws IllegalStateException if file contains invalid TOML
   */
  @Inject
  public TalonProvisioner(Settings settings) {
    Toml toml = settings.getTable(TABLE);
    timeout = toml.getLong("timeout", 0L).intValue();
    logger.info("using config timeout = {} ms", timeout);
    addConfigurations(settings);
  }

  /**
   * Register a new configuration file containing Talon parameters. These parameter objects will be
   * merged with existing parameter objects. If a new parameter object has the same name as an
   * existing object, the old object will be overwritten.
   *
   * @param settings a parsed config collection
   */
  private void addConfigurations(Settings settings) {
    List<Toml> configList = settings.getTables(TALON_TABLE);
    if (configList == null) {
      logger.error("no " + TALON_TABLE + " tables in config");
      return;
    }

    for (Toml config : configList) {
      String name = config.getString(TalonConfigurationBuilder.NAME);
      if (name == null) {
        throw new IllegalArgumentException(TALON_TABLE + " configuration name parameter missing");
      }
      configs.put(name, TalonConfigurationBuilder.create(config));
      logger.info("added configuration: {}", name);
    }
  }

  /**
   * Add a configuration.
   *
   * @param config the configuration to add.
   */
  public void addConfiguration(TalonConfiguration config) {
    String name = config.getName();
    configs.put(name, config);
    logger.info("added configuration: {}", name);
  }

  /**
   * Return Talon parameters for the named configuration.
   *
   * @param name the name of the Talon set of parameters
   * @return configured Talon parameter immutable object
   */
  @NotNull
  public TalonConfiguration configurationFor(String name) {
    TalonConfiguration config = configs.get(name);
    if (config == null) {
      throw new IllegalArgumentException("Talon configuration not found: " + name);
    }
    return config;
  }

  /**
   * Get an unmodifiable snapshot of configuration names.
   *
   * @return the Set of configuration names.
   */
  @NotNull
  public Set<String> getConfigurationNames() {
    return Collections.unmodifiableSet(configs.keySet());
  }

  /**
   * Get an unmodifiable snapshot of configurations.
   *
   * @return the Collection of configurations.
   */
  @NotNull
  public Collection<TalonConfiguration> getConfigurations() {
    return Collections.unmodifiableCollection(configs.values());
  }

  /**
   * TalonSRX configuration timeout should be enabled at robot initialization.
   *
   * @param timeoutEnabled true to enable.
   */
  public void enableTimeout(boolean timeoutEnabled) {
    logger.info("configuration timeout enabled = {}", timeoutEnabled);
    configs.values().forEach(it -> it.setTimeout(timeoutEnabled ? timeout : 0));
  }

  /**
   * Get the current CAN bus configuration timeout.
   *
   * @return the timeout
   */
  public int getTimeout() {
    return timeout;
  }

  @Override
  @NotNull
  public String toString() {
    return "TalonProvisioner{" + "configs=" + configs + '}';
  }
}
