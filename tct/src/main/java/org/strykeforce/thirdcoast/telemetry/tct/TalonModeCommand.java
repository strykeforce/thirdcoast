package org.strykeforce.thirdcoast.telemetry.tct;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import javax.inject.Named;
import org.jline.reader.LineReader;

/**
 * Command to enter mode for working with Talons.
 */
@ParametersAreNonnullByDefault
public class TalonModeCommand extends AbstractCommand {

  public final static String NAME = "Work with Talons";
  private final Menu talonMenu;

  @Inject
  TalonModeCommand(@Named("TALON") Menu talonMenu, LineReader reader) {
    super(NAME, reader);
    this.talonMenu = talonMenu;
  }

  @Override
  public void perform() {
    talonMenu.display();
  }
}
