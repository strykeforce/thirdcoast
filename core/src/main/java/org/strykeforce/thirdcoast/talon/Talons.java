package org.strykeforce.thirdcoast.talon;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.moandjiezana.toml.Toml;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
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
    Toml settingsTable = settings.getTable(TABLE);
    final int timeout = settingsTable.getLong("timeout").intValue();

    List<TalonConfiguration> talonConfigurations = new ArrayList<>();
    for (Toml toml : settings.getTables(TALONS)) {
      TalonConfiguration config = TalonConfiguration.create(toml);
      logger.debug("added config '{}' for talons {}", config.getName(), config.getTalonIds());
      talonConfigurations.add(config);
    }

    for (TalonConfiguration configuration : talonConfigurations) {
      for (Integer id : configuration.getTalonIds()) {
        if (talons.containsKey(id)) {
          logger.error("Talon {} already configured, skipping", id);
          continue;
        }
        TalonSRX talon = factory.create(id);
        configuration.configure(talon, timeout);
        talons.put(id, talon);
      }
    }
    logger.debug("timeout = {}", timeout);
  }

  /**
   * Gets a {@link TalonSRX} with appropriate default values.
   *
   * @param id the device ID of the TalonSRX to create
   * @return the TalonSRX
   */
  @NotNull
  public TalonSRX getTalon(final int id) {
    if (!talons.containsKey(id)) {
      throw new NoSuchElementException("Talon " + id);
    }
    return talons.get(id);
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
