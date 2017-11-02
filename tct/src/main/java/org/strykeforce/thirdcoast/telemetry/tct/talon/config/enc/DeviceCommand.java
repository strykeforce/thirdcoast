package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc;

import javax.inject.Inject;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;

public class DeviceCommand extends AbstractCommand {

  public final static String NAME = "Encoder Device";

  @Inject
  public DeviceCommand(Terminal terminal) {
    super(NAME, terminal);
  }
}
