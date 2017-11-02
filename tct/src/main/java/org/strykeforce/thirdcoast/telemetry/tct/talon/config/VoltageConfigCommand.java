package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import javax.inject.Inject;
import javax.inject.Provider;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.voltage.VoltageMenuComponent;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.voltage.VoltageMenuModule;

/**
 * Configure selected Talons.
 */
@TalonConfigScope
public class VoltageConfigCommand extends AbstractCommand {

  public final static String NAME = "Voltage Limits and Ramp Rates";
  private final Provider<VoltageMenuComponent.Builder> voltageMenuComponentProvider;
  private final TalonSet talonSet;

  @Inject
  public VoltageConfigCommand(TalonSet talonSet,
      Provider<VoltageMenuComponent.Builder> voltageMenuComponentProvider, Terminal terminal) {
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

    VoltageMenuComponent component = voltageMenuComponentProvider.get().build();
    Menu menu = component.menu();
    menu.display();
  }
}
