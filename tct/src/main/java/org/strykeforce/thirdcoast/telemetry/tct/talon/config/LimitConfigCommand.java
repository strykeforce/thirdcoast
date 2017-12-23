package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import javax.inject.Inject;
import javax.inject.Named;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.Messages;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;

/** Configure selected Talons. */
public class LimitConfigCommand extends AbstractCommand {

  public static final String NAME = "Limit Switches";
  private final Menu limitMenu;
  private final TalonSet talonSet;

  @Inject
  public LimitConfigCommand(
      TalonSet talonSet, @Named("TALON_CONFIG_LIM") Menu limitMenu, LineReader reader) {
    super(NAME, reader);
    this.limitMenu = limitMenu;
    this.talonSet = talonSet;
  }

  @Override
  public void perform() {
    if (talonSet.selected().isEmpty()) {
      terminal.writer().println(Messages.bold("no talons selected"));
      return;
    }
    limitMenu.display();
  }
}
