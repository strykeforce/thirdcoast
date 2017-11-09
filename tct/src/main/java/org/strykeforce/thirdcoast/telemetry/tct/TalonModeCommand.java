package org.strykeforce.thirdcoast.telemetry.tct;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.talon.di.TalonMenuComponent;

/**
 * Command to enter mode for working with Talons.
 */
@ParametersAreNonnullByDefault
public class TalonModeCommand extends AbstractCommand {

  public final static String NAME = "Work with Talons";
  private final Provider<TalonMenuComponent.Builder> talonMenuComponentProvider;

  @Inject
  TalonModeCommand(Provider<TalonMenuComponent.Builder> talonMenuComponentProvider,
      LineReader reader) {
    super(NAME, reader);
    this.talonMenuComponentProvider = talonMenuComponentProvider;
  }

  @Override
  public void perform() {
    TalonMenuComponent component = talonMenuComponentProvider.get().build();
    Menu menu = component.menu();
    menu.display();
  }
}
