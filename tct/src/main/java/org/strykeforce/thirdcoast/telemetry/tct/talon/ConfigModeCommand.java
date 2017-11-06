package org.strykeforce.thirdcoast.telemetry.tct.talon;

import javax.inject.Inject;
import javax.inject.Provider;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.di.ModeScoped;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.di.ConfigMenuComponent;
import org.strykeforce.thirdcoast.telemetry.tct.talon.di.TalonMenuModule;

/**
 * Configure selected Talons.
 */
@ModeScoped
public class ConfigModeCommand extends AbstractCommand {

  public final static String NAME = "Configure Selected Talons";
  private final Provider<ConfigMenuComponent.Builder> talonMenuComponentProvider;
  private final TalonSet talonSet;

  @Inject
  public ConfigModeCommand(TalonSet talonSet,
      Provider<ConfigMenuComponent.Builder> talonMenuComponentProvider, LineReader reader) {
    super(NAME, TalonMenuModule.MENU_ORDER.indexOf(NAME), reader);
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
