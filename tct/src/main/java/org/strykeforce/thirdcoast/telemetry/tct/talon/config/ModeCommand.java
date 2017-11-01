package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import javax.inject.Inject;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;

@TalonConfigScope
public class ModeCommand extends AbstactTalonConfigCommand {

  public final static String NAME = "Operating Mode";

  @Inject
  public ModeCommand(TalonSet talonSet, Terminal terminal) {
    super(NAME, terminal, talonSet);
  }

}
