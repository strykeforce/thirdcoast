package org.strykeforce.thirdcoast.telemetry.grapher;

import java.util.Collection;
import javax.inject.Inject;

/**
 * Default implementation of {@link Inventory} for a robot.
 */
public class RobotInventory extends AbstractInventory {

  @Inject
  public RobotInventory(final Collection<Item> items) {
    super(items);
  }

}
