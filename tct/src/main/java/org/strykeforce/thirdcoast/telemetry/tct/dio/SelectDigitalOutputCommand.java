package org.strykeforce.thirdcoast.telemetry.tct.dio;

import javax.inject.Inject;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.Messages;

public class SelectDigitalOutputCommand extends AbstractCommand {

  public final static String NAME = "Select Digital Output";
  private final static Logger logger = LoggerFactory.getLogger(SelectDigitalOutputCommand.class);
  private final DioSet dioSet;

  @Inject
  public SelectDigitalOutputCommand(LineReader reader, DioSet dioSet) {
    super(NAME, reader);
    this.dioSet = dioSet;
  }

  protected static String prompt() {
    return new AttributedStringBuilder()
        .style(AttributedStyle.BOLD.foreground(AttributedStyle.YELLOW))
        .append("digital output channel or <enter> to return> ").toAnsi();
  }


  @Override
  public void perform() {
    terminal.writer().println(Messages.bold("enter digital output channel"));

    String line;
    while (true) {
      try {
        line = reader.readLine(prompt()).trim();
      } catch (EndOfFileException | UserInterruptException e) {
        return;
      }

      if (line.isEmpty()) {
        String msg = "digital output channel selection unchanged";
        logger.info(msg);
        terminal.writer().println(Messages.bold(msg));
        return;
      }
      int id;
      try {
        id = Integer.valueOf(line);
      } catch (NumberFormatException e) {
        terminal.writer().print(Messages.boldRed(String.format("%s is not a number, ignoring%n", line)));
        continue;
      }
      dioSet.selectDigitalOutput(id);
      logger.info("selected digital output channel {}", id);
      break;
    }
  }
}
