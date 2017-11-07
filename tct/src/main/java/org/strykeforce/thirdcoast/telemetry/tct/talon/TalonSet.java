package org.strykeforce.thirdcoast.telemetry.tct.talon;

import com.ctre.CANTalon;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Provider;
import org.strykeforce.thirdcoast.talon.TalonConfigurationBuilder;
import org.strykeforce.thirdcoast.telemetry.tct.di.ModeScoped;

/**
 * Holds Talons being worked on.
 */
@ModeScoped
public class TalonSet {

  final Set<CANTalon> all = new HashSet<>();
  final Set<CANTalon> selected = new HashSet<>();
  private final Provider<TalonConfigurationBuilder> talonConfigurationBuilderProvider;
  private TalonConfigurationBuilder talonConfigurationBuilder;

  @Inject
  public TalonSet(Provider<TalonConfigurationBuilder> talonConfigurationBuilderProvider) {
    this.talonConfigurationBuilderProvider = talonConfigurationBuilderProvider;
    talonConfigurationBuilder = talonConfigurationBuilderProvider.get();
  }

  public Set<CANTalon> all() {
    return all;
  }

  public Set<CANTalon> selected() {
    return selected;
  }

  public Optional<CANTalon> get(int id) {
    return all.stream().filter(it -> it.getDeviceID() == id).findFirst();
  }

  public TalonConfigurationBuilder talonConfigurationBuilder() {
    return talonConfigurationBuilder;
  }
}
