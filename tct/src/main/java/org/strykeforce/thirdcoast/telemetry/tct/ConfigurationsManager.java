package org.strykeforce.thirdcoast.telemetry.tct;

import com.moandjiezana.toml.TomlWriter;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.TalonConfiguration;
import org.strykeforce.thirdcoast.talon.TalonProvisioner;

/**
 * Configuration file operations.
 */
@Singleton
public class ConfigurationsManager {

  private final static Logger logger = LoggerFactory.getLogger(ConfigurationsManager.class);
  private final TalonProvisioner talonProvisioner;
  private final File file;

  /**
   * Construct an instance.
   *
   * @param talonProvisioner the TalonProvisioner used to manage TalonConfigurations
   */
  @Inject
  ConfigurationsManager(TalonProvisioner talonProvisioner, File file) {
    this.talonProvisioner = talonProvisioner;
    this.file = file;
  }

  public TalonProvisioner getTalonProvisioner() {
    return talonProvisioner;
  }

  public void save() {
    Map<String, Collection<TalonConfiguration>> tomlMap = new HashMap<>(2);
    Collection<TalonConfiguration> configs = talonProvisioner.getConfigurations();
    tomlMap.put(TalonProvisioner.TALON_TABLE, configs);
    TomlWriter writer = new TomlWriter();
    try {
      writer.write(tomlMap, file);
    } catch (IOException e) {
      logger.error("error saving configuration", e);
    }
  }
}
