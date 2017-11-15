package org.strykeforce.thirdcoast.telemetry.tct;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import javax.inject.Named;
import org.jline.reader.LineReader;

/**
 * Command to enter mode for working with Dios.
 */
@ParametersAreNonnullByDefault
public class DioModeCommand extends AbstractCommand {

  public final static String NAME = "Work with Digital Outputs";
  private final Menu dioMenu;

  @Inject
  DioModeCommand(@Named("DIO") Menu dioMenu, LineReader reader) {
    super(NAME, reader);
    this.dioMenu = dioMenu;
  }

  @Override
  public void perform() {
    dioMenu.display();
  }
}
