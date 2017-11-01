package org.strykeforce.thirdcoast.telemetry.tct.talon;

import com.ctre.CANTalon;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;

/**
 * Holds Talons being worked on.
 */
@TalonScope
public class TalonSet {

  final Set<CANTalon> all = new HashSet<>();
  final Set<CANTalon> selected = new HashSet<>();

  @Inject
  public TalonSet() {
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

}
