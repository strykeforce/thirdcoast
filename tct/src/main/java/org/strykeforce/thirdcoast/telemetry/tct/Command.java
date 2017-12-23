package org.strykeforce.thirdcoast.telemetry.tct;

import java.util.Optional;

/** Represents a menu command and associated action. */
public interface Command {

  /**
   * Text presented to user in a menu for this command.
   *
   * @return the menu choice.
   */
  String name();

  /** Perform action associated with this command. */
  void perform();

  /** Perform an optional command after this command. */
  Optional<Command> post();
}
