package org.strykeforce.thirdcoast.telemetry.tct;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import javax.inject.Named;
import org.jline.reader.LineReader;

/** Command to enter mode for working with Servos. */
@ParametersAreNonnullByDefault
public class ServoModeCommand extends AbstractCommand {

  public static final String NAME = "Work with Servos";
  private final Menu servoMenu;

  @Inject
  ServoModeCommand(@Named("SERVO") Menu servoMenu, LineReader reader) {
    super(NAME, reader);
    this.servoMenu = servoMenu;
  }

  @Override
  public void perform() {
    servoMenu.display();
  }
}
