package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import javax.inject.Inject;
import javax.inject.Provider;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.EncoderMenuComponent;

/**
 * Configure selected Talons.
 */
@ConfigScope
public class EncoderConfigCommand extends AbstractCommand {

  public final static String NAME = "Encoders, Velocity Measurement and Frame Rates";
  private final Provider<EncoderMenuComponent.Builder> voltageMenuComponentProvider;
  private final TalonSet talonSet;

  @Inject
  public EncoderConfigCommand(TalonSet talonSet,
      Provider<EncoderMenuComponent.Builder> voltageMenuComponentProvider, LineReader reader) {
    super(NAME, ConfigMenuModule.MENU_ORDER.indexOf(NAME), reader);
    this.voltageMenuComponentProvider = voltageMenuComponentProvider;
    this.talonSet = talonSet;
  }

  @Override
  public void perform() {
    if (talonSet.selected().isEmpty()) {
      terminal.writer().println(bold("no talons selected"));
      return;
    }

    EncoderMenuComponent component = voltageMenuComponentProvider.get().build();
    Menu menu = component.menu();
    menu.display();
  }
}
