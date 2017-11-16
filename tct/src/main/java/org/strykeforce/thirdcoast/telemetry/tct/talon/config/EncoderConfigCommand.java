package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import javax.inject.Inject;
import javax.inject.Named;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.Messages;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;

/**
 * Configure selected Talons.
 */
public class EncoderConfigCommand extends AbstractCommand {

  public final static String NAME = "Encoders, Velocity Measurement and Frame Rates";
  private final Menu encoderMenu;
  private final TalonSet talonSet;

  @Inject
  public EncoderConfigCommand(TalonSet talonSet, @Named("TALON_CONFIG_ENC") Menu encoderMenu,
      LineReader reader) {
    super(NAME, reader);
    this.talonSet = talonSet;
    this.encoderMenu = encoderMenu;
  }

  @Override
  public void perform() {
    if (talonSet.selected().isEmpty()) {
      terminal.writer().println(Messages.NO_TALONS);
      return;
    }
    encoderMenu.display();
  }
}
