package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import javax.inject.Inject;
import javax.inject.Provider;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.out.OutputMenuComponent;

/**
 * Configure selected Talons.
 */
@ConfigScope
public class OutputConfigCommand extends AbstractCommand {

  public final static String NAME = "Output Limits and Ramp Rates";
  private final Provider<OutputMenuComponent.Builder> voltageMenuComponentProvider;
  private final TalonSet talonSet;

  @Inject
  public OutputConfigCommand(TalonSet talonSet,
      Provider<OutputMenuComponent.Builder> voltageMenuComponentProvider, Terminal terminal) {
    super(NAME, ConfigMenuModule.MENU_ORDER.indexOf(NAME), terminal);
    this.voltageMenuComponentProvider = voltageMenuComponentProvider;
    this.talonSet = talonSet;
  }

  @Override
  public void perform() {
    if (talonSet.selected().isEmpty()) {
      terminal.writer().println(bold("no talons selected"));
      return;
    }

    OutputMenuComponent component = voltageMenuComponentProvider.get().build();
    Menu menu = component.menu();
    menu.display();
  }
}
