package org.strykeforce.thirdcoast.talon;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.moandjiezana.toml.Toml;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.strykeforce.thirdcoast.util.Settings;

/** Instantiate {@link TalonSRX} instances with defaults. */
@Singleton
@ParametersAreNonnullByDefault
public class Talons {

  //  private static final Logger logger = LoggerFactory.getLogger(TalonFactory.class);
  private static final String TABLE = "THIRDCOAST.TALONS";
  private static final String TALONS = "TALON";
  private final Map<Integer, TalonSRX> talons = new HashMap<>();

  @Inject
  public Talons(Settings settings, Factory factory) {
    Toml toml = settings.getTable(TABLE);
    final int timeout = toml.getLong("timeout", 10L).intValue();
    List<TalonConfiguration> talonConfigurations =
        settings
            .getTables(TALONS)
            .stream()
            .map(TalonConfiguration::create)
            .collect(Collectors.toList());

    for (TalonConfiguration configuration : talonConfigurations) {
      for (Integer id : configuration.getTalonIds()) {
        if (talons.containsKey(id)) {
          throw new IllegalStateException("Talon " + id + " already configured");
        }
        TalonSRX talon = factory.create(id);
        configuration.configure(talon, timeout);
        talons.put(id, talon);
      }
    }
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
      return new TalonSRX(id);
    }
  }
}
