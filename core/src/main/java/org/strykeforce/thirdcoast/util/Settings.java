package org.strykeforce.thirdcoast.util;

import com.moandjiezana.toml.Toml;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class Settings {

  private static final Logger logger = LoggerFactory.getLogger(Settings.class);
  private static final String DEFAULTS = "/META-INF/thirdcoast/defaults.toml";

  private final Toml toml;
  private final Toml defaults;

  @Inject
  public Settings(URL config) {
    defaults = defaults();

    if (config == null) {
      logger.warn("Third Coast settings are missing, using defaults");
      this.toml = defaults;
      return;
    }

    Toml toml = defaults;
    try {
      toml = new Toml(defaults).read(config.openStream());
      logger.info("reading Third Coast settings from '{}'", config);
    } catch (IOException e) {
      logger.error("unable to read Third Coast settings from '{}', using defaults", config);
    }

    this.toml = toml;
  }

  public Settings(String tomlString) {
    defaults = defaults();
    toml = new Toml(defaults).read(tomlString);
  }

  public Settings() {
    this("");
  }

  /**
   * Get a table from the settings, with shallow merging.
   *
   * @param key the table key
   * @return the merged table
   */
  public Toml getTable(String key) {
    if (!toml.contains(key)) {
      logger.error("table with key '{}' not present", key);
      return new Toml();
    }
    Toml table = toml.getTable(key);
    Toml defaultTable = defaults.getTable(key);

    return new Toml(defaultTable).read(table);
  }

  public List<Toml> getTables(String key) {
    if (!toml.contains(key)) {
      logger.warn("table array with key '{}' not present", key);
      return Collections.emptyList();
    }
    return toml.getTables(key);
  }

  private Toml defaults() {
    InputStream in = this.getClass().getResourceAsStream(DEFAULTS);
    return new Toml().read(in);
  }
}
