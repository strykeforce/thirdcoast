package org.strykeforce.thirdcoast.telemetry.grapher;

import com.ctre.CANTalon;
import java.util.Collection;
import java.util.stream.Collectors;
import javax.inject.Inject;

/**
 * Default implementation of {@link Inventory} for a robot.
 */
public class RobotInventory extends AbstractInventory {

  public RobotInventory(Collection<Item> items) {
    super(items);
  }

  public static Inventory of(Collection<CANTalon> talons) {
    Collection<Item> items = talons.stream().map(t -> new TalonItem(t))
        .collect(Collectors.toList());
    return new RobotInventory(items);
  }



}
