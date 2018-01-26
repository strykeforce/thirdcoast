package org.strykeforce.thirdcoast.talon;

import com.moandjiezana.toml.Toml;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
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

/**
 * A service for provisioning Talons. This class will read TalonSRX configurations located in TOML
 * configuration objects registered using the {@link #addConfigurations(Toml)} class method.
 *
 * <p>Multiple configurations can be registered by calling the {@code register} method repeatedly.
 *
 * @see com.ctre.phoenix.motorcontrol.can.TalonSRX
 */
@Singleton
@ParametersAreNonnullByDefault
public class TalonProvisioner {

  public static final String TALON_TABLE = "TALON";
  static final int TIMEOUT_MS = 10;
  private static final String DEFAULTS = "/META-INF/thirdcoast/defaults.toml";

  private static final Logger logger = LoggerFactory.getLogger(TalonProvisioner.class);

  @NotNull private final Map<String, TalonConfiguration> settings = new HashMap<>();

  /**
   * Construct the TalonProvisioner with base talon configurations that include swerve drive motors.
   *
   * @param config base configuration that should include swerve azimuth and drive configs
   * @throws IllegalStateException if file contains invalid TOML
   */
  @Inject
  public TalonProvisioner(File config) {
    Toml toml;
    if (Files.notExists(config.toPath())) {
      logger.warn("{} is missing, using defaults in " + DEFAULTS, config);
      toml = defaults();
    } else {
      toml = new Toml(defaults()).read(config);
      logger.info("adding configurations from {}", config);
    }
    addConfigurations(toml);
  }

  private Toml defaults() {
    InputStream in = this.getClass().getResourceAsStream(DEFAULTS);
    return new Toml().read(in);
  }

  /**
   * Register a new configuration file containing Talon parameters. These parameter objects will be
   * merged with existing parameter objects. If a new parameter object has the same name as an
   * existing object, the old object will be overwritten.
   *
   * @param configs a parsed config collection
   */
  public void addConfigurations(Toml configs) {
    List<Toml> configList = configs.getTables(TALON_TABLE);
    if (configList == null) {
      logger.error("no " + TALON_TABLE + " tables in config");
      return;
    }

    for (Toml config : configList) {
      String name = config.getString(TalonConfigurationBuilder.NAME);
      if (name == null) {
        throw new IllegalArgumentException(TALON_TABLE + " configuration name parameter missing");
      }
      settings.put(name, TalonConfigurationBuilder.create(config));
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
    settings.put(name, config);
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
    TalonConfiguration config = settings.get(name);
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
    return Collections.unmodifiableSet(settings.keySet());
  }

  /**
   * Get an unmodifiable snapshot of configurations.
   *
   * @return the Collection of configurations.
   */
  @NotNull
  public Collection<TalonConfiguration> getConfigurations() {
    return Collections.unmodifiableCollection(settings.values());
  }

  /**
   * TalonSRX configuration timeout should be enabled at robot initialization.
   *
   * @param timeoutEnabled true to enable.
   */
  public void enableTimeout(boolean timeoutEnabled) {
    logger.info("configuration timeout enabled = {}", timeoutEnabled);
    settings.values().forEach(it -> it.setTimeout(timeoutEnabled ? TIMEOUT_MS : 0));
  }

  @Override
  @NotNull
  public String toString() {
    return "TalonProvisioner{" + "settings=" + settings + '}';
  }
}
