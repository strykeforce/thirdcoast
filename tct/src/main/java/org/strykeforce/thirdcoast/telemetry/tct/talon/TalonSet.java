package org.strykeforce.thirdcoast.telemetry.tct.talon;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.TalonConfiguration;
import org.strykeforce.thirdcoast.talon.TalonConfigurationBuilder;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

/** Holds Talons being worked on. Talons that have been instantiated are cached. */
@Singleton
@ParametersAreNonnullByDefault
public class TalonSet {

  private static final Logger logger = LoggerFactory.getLogger(TalonSet.class);
  private final Set<TalonSRX> selected = new HashSet<>();
  private final TelemetryService telemetryService;
  private TalonConfigurationBuilder talonConfigurationBuilder;

  @Inject
  public TalonSet(TelemetryService telemetryService) {
    this.telemetryService = telemetryService;
    talonConfigurationBuilder = new TalonConfigurationBuilder();
  }

  public TalonConfiguration getActiveTalonConfiguration() {
    return talonConfigurationBuilder.build();
  }

  void setActiveTalonConfiguration(TalonConfiguration activeTalonConfiguration) {
    talonConfigurationBuilder = new TalonConfigurationBuilder(activeTalonConfiguration);
  }

  public void selectTalon(TalonSRX talon) {
    selected.add(talon);
  }

  void clearSelected() {
    selected.clear();
  }

  public Set<TalonSRX> selected() {
    return Collections.unmodifiableSet(selected);
  }

  Set<Integer> getSelectedTalonIds() {
    return selected.stream().map(TalonSRX::getDeviceID).collect(Collectors.toSet());
  }

  public Optional<TalonSRX> get(int id) {
    return selected.stream().filter(it -> it.getDeviceID() == id).findFirst();
  }

  void restartTelemetryService() {
    logger.info("restarting TelemetryService");
    telemetryService.stop();
    selected.forEach(telemetryService::register);
    telemetryService.start();
  }

  public TalonConfigurationBuilder talonConfigurationBuilder() {
    if (talonConfigurationBuilder == null) {
      talonConfigurationBuilder = new TalonConfigurationBuilder();
    }
    return talonConfigurationBuilder;
  }
}
