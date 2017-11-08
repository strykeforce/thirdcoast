package org.strykeforce.thirdcoast.telemetry.tct.talon;

import com.ctre.CANTalon;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.TalonConfiguration;
import org.strykeforce.thirdcoast.talon.TalonConfigurationBuilder;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.tct.di.ModeScoped;

/**
 * Holds Talons being worked on. Talons that have been instantiated are cached.
 */
@ModeScoped
public class TalonSet {

  final static Logger logger = LoggerFactory.getLogger(TalonSet.class);
  private final Set<CANTalon> selected = new HashSet<>();
  private final TelemetryService telemetryService;
  private TalonConfigurationBuilder talonConfigurationBuilder;
  private TalonConfiguration activeTalonConfiguration;

  @Inject
  public TalonSet(TelemetryService telemetryService) {
    this.telemetryService = telemetryService;
  }

  public TalonConfiguration getActiveTalonConfiguration() {
    return activeTalonConfiguration;
  }

  public void setActiveTalonConfiguration(TalonConfiguration activeTalonConfiguration) {
    this.activeTalonConfiguration = activeTalonConfiguration;
    talonConfigurationBuilder = new TalonConfigurationBuilder(activeTalonConfiguration);
  }

  public void selectTalon(CANTalon talon) {
    selected.add(talon);
  }

  public void clearSelected() {
    selected.clear();
  }

  public Set<CANTalon> selected() {
    return Collections.unmodifiableSet(selected);
  }

  public Set<Integer> getSelectedTalonIds() {
    return selected.stream().map(CANTalon::getDeviceID).collect(Collectors.toSet());
  }

  public Optional<CANTalon> get(int id) {
    return selected.stream().filter(it -> it.getDeviceID() == id).findFirst();
  }

  public void restartTelemetryService() {
    logger.info("restarting TelemetryService");
    telemetryService.stop();
    telemetryService.clear();
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
