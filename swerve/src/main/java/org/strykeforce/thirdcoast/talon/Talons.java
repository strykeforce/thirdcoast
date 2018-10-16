package org.strykeforce.thirdcoast.talon;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.util.Settings;

/** Instantiate {@link TalonSRX} instances with defaults. */
@Singleton
@ParametersAreNonnullByDefault
public class Talons {

  private static final Logger logger = LoggerFactory.getLogger(Talons.class);
  private static final String TABLE = "THIRDCOAST.TALONS";
  private static final String TALONS = "TALON";
  private final Map<Integer, TalonSRX> talons = new HashMap<>();

  @Inject
  public Talons(Settings settings, Factory factory) {
    logger.info("loading settings from '{}'", TABLE);
    Toml settingsTable = settings.getTable(TABLE);

    final int timeout = settingsTable.getLong("timeout").intValue();
    logger.debug("TalonSRX configuration timeout = {}", timeout);

    boolean summarizeErrors = settingsTable.getBoolean("summarizeErrors", false);
    Errors.setSummarized(summarizeErrors);
    logger.debug("TalonSRX configuration errors summarized = {}", summarizeErrors);

    boolean logConfig = settingsTable.getBoolean("logConfig", false);
    TomlWriter writer = logConfig ? new TomlWriter() : null;

    List<TalonConfiguration> talonConfigurations = new ArrayList<>();
    List<Toml> talonConfigTables = settings.getTables(TALONS);
    if (talonConfigTables.size() == 0) logger.warn("no TalonSRX configurations available");
    else
      logger.info(
          "loading {} TalonSRX configurations from '{}' table array",
          talonConfigTables.size(),
          TALONS);

    for (Toml toml : talonConfigTables) {
      TalonConfiguration config = TalonConfiguration.create(toml);
      if (logConfig) {
        Map<String, Object> dump = new HashMap<>(1);
        dump.put("TALON", TalonConfiguration.dump(toml));
        logger.info("\n" + writer.write(dump));
      }
      talonConfigurations.add(config);
      logger.debug("added '{}' for TalonSRX ids: {}", config.getName(), config.getTalonIds());
    }

    Errors.setCount(0);
    for (TalonConfiguration config : talonConfigurations) {
      for (Integer id : config.getTalonIds()) {
        if (talons.containsKey(id)) {
          logger.error("TalonSRX {} already configured, ignoring '{}'", id, config.getName());
          continue;
        }
        TalonSRX talon = factory.create(id);
        config.configure(talon, timeout);
        talons.put(id, talon);
      }
    }
    int errorCount = Errors.getCount();
    if (errorCount > 0) logger.error("TalonSRX configuration error count = {}", errorCount);
  }

  /**
   * Gets a {@link TalonSRX} with appropriate default values.
   *
   * @param id the device ID of the TalonSRX to create
   * @return the TalonSRX
   */
  public TalonSRX getTalon(int id) {
    if (!talons.containsKey(id)) {
      logger.error("TalonSRX {} not found", id);
    }
    return talons.get(id);
  }

  /**
   * Convenience method to get ordered list of {@link TalonSRX}.
   *
   * @param ids List of TalonSRX ids
   * @return List of TalonSRX objects
   */
  public List<TalonSRX> getTalons(List<? extends Integer> ids) {
    return ids.stream().map(this::getTalon).collect(Collectors.toList());
  }

  static class Factory {
    @Inject
    Factory() {}

    TalonSRX create(int id) {
      logger.debug("creating TalonSRX with id = {}", id);
      return new TalonSRX(id);
    }
  }
}
