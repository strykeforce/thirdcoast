package org.strykeforce.thirdcoast.telemetry.tct.talon;

import com.ctre.CANTalon;
import java.io.PrintWriter;
import java.util.Optional;
import javax.inject.Inject;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.di.ModeScoped;
import org.strykeforce.thirdcoast.telemetry.tct.talon.di.TalonMenuModule;

@ModeScoped
public class InspectCommand extends AbstractCommand {

  public final static String NAME = "Inspect Talon";
  final static Logger logger = LoggerFactory.getLogger(InspectCommand.class);
  private final TalonSet talonSet;

  @Inject
  public InspectCommand(TalonSet talonSet, LineReader reader) {
    super(NAME, TalonMenuModule.MENU_ORDER.indexOf(NAME), reader);
    this.talonSet = talonSet;
  }

  @Override
  public void perform() {
    PrintWriter writer = terminal.writer();
    Optional<CANTalon> opt = getTalon();
    if (!opt.isPresent()) {
      logger.info("no talon selected");
      return;
    }
    CANTalon talon = opt.get();
    logger.info("inspecting {}", talon.getDescription());
    writer.println();
    writer.printf("         %s %-2d                  %s %6.3f           %s %6.3f%n",
        bold("ID:"), talon.getDeviceID(), bold("P:"), talon.getP(), bold("I-zone:"),
        talon.getIZone());
    writer.printf("%s %13s       %s %6.3f      %s  %-6d%n",
        bold("Description:"), talon.getDescription(), bold("I:"), talon.getI(),
        bold("Encoder Pos:"), talon.getEncPosition());
    writer.printf("       %s %-13s       %s %6.3f%n", bold("Mode:"),
        talon.getControlMode(), bold("D:"), talon.getD());
    writer.printf("                                 %s %6.3f%n",
        bold("F:"), talon.getF());
    writer.println();
    writer.printf("%s %s       %s %5.2f%n",
        bold("Velocity Meas. Period:"), talon.GetVelocityMeasurementPeriod(),
        bold("Closed-loop Ramp Rate:"), talon.getCloseLoopRampRate());
    writer.printf("%s %d              %s %5.2f%n",
        bold("Velocity Meas. Window:"),
        talon.GetVelocityMeasurementWindow(), bold("Nom. Closed-loop Voltage:"),
        talon.GetNominalClosedLoopVoltage());
    writer.printf("                                                    %s %5.2f%n",
        bold("Bus Voltage:"), talon.getBusVoltage());
    writer.println();
  }

  private Optional<CANTalon> getTalon() {
    Optional<CANTalon> talon = Optional.empty();
    while (!talon.isPresent()) {
      String line = null;
      try {
        line = reader.readLine(boldYellow("talon id> ")).trim();
      } catch (EndOfFileException | UserInterruptException e) {
        break;
      }

      if (line.isEmpty()) {
        logger.info("no talon selected");
        break;
      }

      int id;
      try {
        id = Integer.valueOf(line);
      } catch (NumberFormatException e) {
        terminal.writer().println(bold("please enter a number"));
        continue;
      }
      talon = talonSet.get(id);
    }
    return talon;
  }
}
