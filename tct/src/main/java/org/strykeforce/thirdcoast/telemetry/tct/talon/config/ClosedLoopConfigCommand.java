package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import javax.inject.Inject;
import javax.inject.Named;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.Messages;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;

/** Configure selected Talons. */
public class ClosedLoopConfigCommand extends AbstractCommand {

  public static final String NAME = "Closed-Loop Tuning";
  private final Menu closedLoopMenu;
  private final TalonSet talonSet;

  @Inject
  public ClosedLoopConfigCommand(
      TalonSet talonSet, @Named("TALON_CONFIG_CL") Menu closedLoopMenu, LineReader reader) {
    super(NAME, reader);
    this.talonSet = talonSet;
    this.closedLoopMenu = closedLoopMenu;
  }

  @Override
  public void perform() {
    if (talonSet.selected().isEmpty()) {
      terminal.writer().println(Messages.NO_TALONS);
      return;
    }

    closedLoopMenu.display();
  }
}
