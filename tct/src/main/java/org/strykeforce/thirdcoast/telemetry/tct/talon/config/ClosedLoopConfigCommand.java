package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import javax.inject.Inject;
import javax.inject.Provider;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl.ClosedLoopMenuComponent;

/**
 * Configure selected Talons.
 */
@ConfigScope
public class ClosedLoopConfigCommand extends AbstractCommand {

  public final static String NAME = "Closed-Loop Tuning";
  private final Provider<ClosedLoopMenuComponent.Builder> closedLoopMenuComponentProvider;
  private final TalonSet talonSet;

  @Inject
  public ClosedLoopConfigCommand(TalonSet talonSet,
      Provider<ClosedLoopMenuComponent.Builder> closedLoopMenuComponentProvider,
      Terminal terminal) {
    super(NAME, ConfigMenuModule.MENU_ORDER.indexOf(NAME), terminal);
    this.closedLoopMenuComponentProvider = closedLoopMenuComponentProvider;
    this.talonSet = talonSet;
  }

  @Override
  public void perform() {
    if (talonSet.selected().isEmpty()) {
      terminal.writer().println(bold("no talons selected"));
      return;
    }

    ClosedLoopMenuComponent component = closedLoopMenuComponentProvider.get().build();
    Menu menu = component.menu();
    menu.display();
  }
}
