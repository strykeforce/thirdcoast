package org.strykeforce.thirdcoast.telemetry.tct.talon;

import com.ctre.CANTalon;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.TalonConfiguration;
import org.strykeforce.thirdcoast.talon.TalonConfigurationBuilder;
import org.strykeforce.thirdcoast.talon.TalonFactory;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.Command;

/**
 * Select Talons to work with.
 */
@ParametersAreNonnullByDefault
public class SelectCommand extends AbstractCommand {

  public final static String NAME = "Select Talons to Work With";
  private final static Logger logger = LoggerFactory.getLogger(SelectCommand.class);
  private final TalonFactory talonFactory;
  private final TalonSet talonSet;
  private final Command listCommand;

  @Inject
  SelectCommand(TalonSet talonSet, TalonFactory talonFactory, LineReader reader,
      ListCommand listCommand) {
    super(NAME, reader);
    this.talonSet = talonSet;
    this.talonFactory = talonFactory;
    this.listCommand = listCommand;
  }

  protected static String prompt() {
    return new AttributedStringBuilder()
        .style(AttributedStyle.BOLD.foreground(AttributedStyle.YELLOW))
        .append("talon IDs or <enter> to return> ").toAnsi();
  }

  @Override
  public void perform() {
    terminal.writer().println(bold("enter comma-separated list of Talon IDs"));

    String line;
    try {
      line = reader.readLine(prompt()).trim();
    } catch (EndOfFileException | UserInterruptException e) {
      return;
    }

    if (line.isEmpty()) {
      String msg = "Talon selection unchanged";
      logger.info(msg);
      terminal.writer().println(bold(msg));
      return;
    }
    TalonConfigurationBuilder builder = talonSet.talonConfigurationBuilder();
    TalonConfiguration config = builder.build();
    talonSet.setActiveTalonConfiguration(config);
    talonSet.clearSelected();
    List<String> ids = Arrays.asList(line.split(","));
    for (String s : ids) {
      int id;
      try {
        id = Integer.valueOf(s);
      } catch (NumberFormatException e) {
        terminal.writer().print(bold(String.format("%s is not a number, ignoring%n", s)));
        continue;
      }
      CANTalon talon = talonFactory.getTalon(id);
      config.configure(talon);
      talonSet.selectTalon(talon);
      logger.info("selected talon id {} with config {}", id, config.getName());
    }
    talonSet.restartTelemetryService();
  }

  @NotNull
  @Override
  public Optional<Command> post() {
    return Optional.of(listCommand);
  }
}
