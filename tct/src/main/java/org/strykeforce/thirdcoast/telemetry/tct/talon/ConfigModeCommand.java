package org.strykeforce.thirdcoast.telemetry.tct.talon;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import javax.inject.Named;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;

/**
 * Configure selected Talons.
 */
@ParametersAreNonnullByDefault
public class ConfigModeCommand extends AbstractCommand {

  public final static String NAME = "Configure Selected Talons";
  private final Menu talonConfigMenu;
  private final TalonSet talonSet;

  @Inject
  ConfigModeCommand(TalonSet talonSet, @Named("TALON_CONFIG") Menu talonConfigMenu,
      LineReader reader) {
    super(NAME, reader);
    this.talonSet = talonSet;
    this.talonConfigMenu = talonConfigMenu;
  }

  @Override
  public void perform() {
    if (talonSet.selected().isEmpty()) {
      terminal.writer().println(bold("no talons selected"));
      return;
    }
    talonConfigMenu.display();
  }
}
