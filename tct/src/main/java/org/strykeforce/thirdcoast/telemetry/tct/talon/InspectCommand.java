package org.strykeforce.thirdcoast.telemetry.tct.talon;

import com.ctre.CANTalon;
import java.io.PrintWriter;
import java.util.Optional;
import javax.inject.Inject;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;

@TalonScope
public class InspectCommand extends AbstractCommand {

  public final static String NAME = "Inspect Talon";
  final static Logger logger = LoggerFactory.getLogger(InspectCommand.class);
  private final LineReader reader;
  private final TalonSet talonSet;

  @Inject
  public InspectCommand(TalonSet talonSet, Terminal terminal) {
    super(NAME, terminal);
    this.talonSet = talonSet;
    reader = LineReaderBuilder.builder().terminal(terminal).build();
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
    writer
        .printf("         ID: %-2d                  P: %6.3f%n", talon.getDeviceID(), talon.getP());
    writer.printf("Description: %13s       I: %6.3f%n", talon.getDescription(), talon.getI());
    writer.printf("       Mode: %-13s       D: %6.3f%n", talon.getControlMode(), talon.getD());
    writer.printf("                                 F: %6.3f%n", talon.getF());
    writer.printf("                            I-zone: %6.3f%n", talon.getIZone());
    writer.printf("                       Encoder Pos:  %-6d%n", talon.getEncPosition());
    writer.println();
    writer.printf("Velocity Meas. Period: %s%n", talon.GetVelocityMeasurementPeriod());
    writer.printf("Velocity Meas. Window: %d%n", talon.GetVelocityMeasurementWindow());
    writer.println();
    writer.printf("   Closed-loop Ramp Rate: %5.2f%n", talon.GetNominalClosedLoopVoltage());
    writer.printf("Nom. Closed-loop Voltage: %5.2f%n", talon.getCloseLoopRampRate());
    writer.printf("             Bus Voltage: %5.2f%n", talon.getBusVoltage());
    writer.println();
  }

  private Optional<CANTalon> getTalon() {
    Optional<CANTalon> talon = Optional.empty();
    terminal.writer().println("enter ID of Talon to inspect or <enter> to go back");

    while (!talon.isPresent()) {
      String line = null;
      try {
        line = reader.readLine("talon id> ").trim();
      } catch (EndOfFileException | UserInterruptException e) {
        break;
      }

      if (line.isEmpty()) {
        logger.info("no talon selected");
        break;
      }

      int id = -1;
      try {
        id = Integer.valueOf(line);
      } catch (NumberFormatException e) {
        continue;
      }
      talon = talonSet.get(id);
    }
    return talon;
  }
}
