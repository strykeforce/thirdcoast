package org.strykeforce.thirdcoast.telemetry.tct;

import javax.inject.Inject;
import javax.inject.Provider;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonMenuComponent;

/**
 * Command to enter mode for working with Talons.
 */
public class TalonModeCommand extends AbstractCommand {

  public final static String NAME = "Work with Talons";
  private final Provider<TalonMenuComponent.Builder> talonMenuComponentProvider;

  @Inject
  public TalonModeCommand(Provider<TalonMenuComponent.Builder> talonMenuComponentProvider,
      Terminal terminal) {
    super(NAME, terminal);
    this.talonMenuComponentProvider = talonMenuComponentProvider;
  }

  @Override
  public void perform() {
    TalonMenuComponent component = talonMenuComponentProvider.get().build();
    Menu menu = component.menu();
    menu.display();
  }
}
