package org.strykeforce.thirdcoast.telemetry.tct.talon;

import javax.inject.Inject;
import javax.inject.Provider;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.ModeScope;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.ConfigMenuComponent;

/**
 * Configure selected Talons.
 */
@ModeScope
public class ConfigCommand extends AbstractCommand {

  public final static String NAME = "Configure Selected Talons";
  private final Provider<ConfigMenuComponent.Builder> talonMenuComponentProvider;
  private final TalonSet talonSet;

  @Inject
  public ConfigCommand(TalonSet talonSet,
      Provider<ConfigMenuComponent.Builder> talonMenuComponentProvider, Terminal terminal) {
    super(NAME, TalonMenuModule.MENU_ORDER.indexOf(NAME), terminal);
    this.talonMenuComponentProvider = talonMenuComponentProvider;
    this.talonSet = talonSet;
  }

  @Override
  public void perform() {
    if (talonSet.selected.isEmpty()) {
      terminal.writer().println(bold("no talons selected"));
      return;
    }

    ConfigMenuComponent component = talonMenuComponentProvider.get().build();
    Menu menu = component.menu();
    menu.display();
  }
}
