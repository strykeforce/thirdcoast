package org.strykeforce.thirdcoast.telemetry.tct.talon;

import com.ctre.CANTalon;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.ModeScope;

/**
 * Display a list of all Talons.
 */
@ModeScope
public class ListCommand extends AbstractCommand {

  public final static String NAME = "List Talons";
  private final TalonSet talonSet;

  @Inject
  public ListCommand(TalonSet talonSet, LineReader reader) {
    super(NAME, TalonMenuModule.MENU_ORDER.indexOf(NAME), reader);
    this.talonSet = talonSet;
  }

  @Override
  public void perform() {
    terminal.writer().printf(header());
    for (CANTalon talon : talonSet.all) {
      terminal.writer().printf("%d: %15s %8s %2s%n", talon.getDeviceID(), talon.getDescription(),
          talon.getControlMode(), talonSet.selected.contains(talon) ? "Y" : "");
    }
    terminal.writer().println();
  }

  private String header() {
    String header = String.format("ID %15s %8s Sel.%n", "Talon", "Mode");
    return new AttributedStringBuilder().style(AttributedStyle.BOLD).append(header).toAnsi();
  }

}
