package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import javax.inject.Inject;
import javax.inject.Named;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;

/**
 * Configure selected Talons.
 */
public class OutputConfigCommand extends AbstractCommand {

  public final static String NAME = "Output Limits and Ramp Rates";
  private final Menu outputMenu;
  private final TalonSet talonSet;

  @Inject
  public OutputConfigCommand(TalonSet talonSet, @Named("TALON_CONFIG_OUT") Menu outputMenu,
      LineReader reader) {
    super(NAME, reader);
    this.outputMenu = outputMenu;
    this.talonSet = talonSet;
  }

  @Override
  public void perform() {
    if (talonSet.selected().isEmpty()) {
      terminal.writer().println(bold("no talons selected"));
      return;
    }
    outputMenu.display();
  }
}
