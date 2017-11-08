package org.strykeforce.thirdcoast.talon;

import com.moandjiezana.toml.Toml;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A service for provisioning Talons. This class will read CANTalon configurations located in TOML
 * configuration objects registered using the {@link #addConfigurations(Toml)} class method.
 *
 * <p>Multiple configurations can be registered by calling the {@code register} method repeatedly.
 *
 * @see com.ctre.CANTalon
 */
@Singleton
public class TalonProvisioner {

  public final static String TALON_TABLE = "TALON";
  private final static String DEFAULT_CONFIG = "/org/strykeforce/thirdcoast/defaults.toml";

  final static Logger logger = LoggerFactory.getLogger(TalonProvisioner.class);

  private final Map<String, TalonConfiguration> settings = new HashMap<>();

  /**
   * Construct the TalonProvisioner with base talon configurations that include swerve drive
   * motors.
   *
   * @param file base configuration that should include swerve azimuth and drive configs
   * @throws IllegalStateException if file contains invalid TOML
   */
  @Inject
  public TalonProvisioner(File file) {
    checkFileExists(file);
    Toml toml = new Toml().read(file);
    addConfigurations(toml);
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
      System.out.println("name = " + name);
      if (name == null) {
        throw new IllegalArgumentException(TALON_TABLE + " configuration name parameter missing");
      }
      settings.put(name, TalonConfigurationBuilder.create(config));
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

  static void checkFileExists(File file) {
    Path path = file.toPath();
    if (Files.notExists(path)) {
      InputStream is = TalonProvisioner.class.getResourceAsStream(DEFAULT_CONFIG);
      try {
        Files.copy(is, path);
      } catch (IOException e) {
        logger.error("unable to copy default config to " + path, e);
      }
    }
  }

  @Override
  public String toString() {
    return "TalonProvisioner{" +
        "settings=" + settings +
        '}';
  }
}
