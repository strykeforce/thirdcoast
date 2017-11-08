package org.strykeforce.thirdcoast.telemetry.tct.talon;

import javax.inject.Inject;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.TalonConfiguration;
import org.strykeforce.thirdcoast.talon.TalonConfigurationBuilder;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.ConfigurationsManager;
import org.strykeforce.thirdcoast.telemetry.tct.talon.di.TalonMenuModule;

public class SaveConfigCommand extends AbstractCommand {

  public final static String NAME = "Save Active Configuration";
  final static Logger logger = LoggerFactory.getLogger(SaveConfigCommand.class);
  private final ConfigurationsManager configurationsManager;
  private final TalonSet talonSet;

  @Inject
  public SaveConfigCommand(LineReader reader, TalonSet talonSet,
      ConfigurationsManager configurationsManager) {
    super(NAME, TalonMenuModule.MENU_ORDER.indexOf(NAME), reader);
    this.configurationsManager = configurationsManager;
    this.talonSet = talonSet;
  }

  protected static String prompt() {
    return new AttributedStringBuilder()
        .style(AttributedStyle.BOLD.foreground(AttributedStyle.YELLOW))
        .append("configuration name> ").toAnsi();
  }

  @Override
  public void perform() {
    terminal.writer().println(bold("enter name to save configuration with" +
        " or <enter> to return without saving"));
    String name = null;
    try {
      name = reader.readLine(prompt()).trim();
    } catch (EndOfFileException | UserInterruptException e) {
      return;
    }

    if (name.isEmpty()) {
      String msg = "configuration was not saved";
      logger.info(msg);
      terminal.writer().println(bold(msg));
      return;
    }

    TalonConfigurationBuilder builder = talonSet.talonConfigurationBuilder();
    TalonConfiguration config = builder.name(name).build();
    config.addAllTalonIds(talonSet.getSelectedTalonIds());
    talonSet.setActiveTalonConfiguration(config);
    configurationsManager.getTalonProvisioner().addConfiguration(config);
    configurationsManager.save();
    terminal.writer().println(bold("saved configuration: " + name));
  }
}
