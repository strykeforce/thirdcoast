package org.strykeforce.thirdcoast.telemetry.tct.command;

import org.jline.utils.AttributedString;

/**
 * Represents a command that can be run in the application.
 */
public interface Command {

  AttributedString prompt();

  void run();

}
