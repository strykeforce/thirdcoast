package org.strykeforce.thirdcoast.telemetry.tct.talon;

import java.util.Collection;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.TalonConfiguration;
import org.strykeforce.thirdcoast.talon.TalonConfigurationBuilder;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.ConfigurationsManager;
import org.strykeforce.thirdcoast.telemetry.tct.Messages;

@ParametersAreNonnullByDefault
public class SaveConfigCommand extends AbstractCommand {

  public final static String NAME = "Save Active Configuration";
  private final static Logger logger = LoggerFactory.getLogger(SaveConfigCommand.class);
  private final ConfigurationsManager configurationsManager;
  private final TalonSet talonSet;

  @Inject
  SaveConfigCommand(LineReader reader, TalonSet talonSet,
      ConfigurationsManager configurationsManager) {
    super(NAME, reader);
    this.configurationsManager = configurationsManager;
    this.talonSet = talonSet;
  }

  @Override
  public void perform() {
    terminal.writer().println(Messages.bold("Saved Configurations:"));
    Collection<TalonConfiguration> configs = configurationsManager.getTalonProvisioner()
        .getConfigurations();
    for (TalonConfiguration config : configs) {
      terminal.writer().println("  - " + config.getName());
    }
    terminal.writer().println();
    terminal.writer().println(Messages.bold("enter name to save configuration with" +
        " or <enter> to return without saving"));
    String name;
    try {
      name = reader.readLine(Messages.prompt("configuration name> ")).trim();
    } catch (EndOfFileException | UserInterruptException e) {
      return;
    }

    if (name.isEmpty()) {
      String msg = "configuration was not saved";
      logger.info(msg);
      terminal.writer().println(Messages.bold(msg));
      return;
    }

    TalonConfigurationBuilder builder = talonSet.talonConfigurationBuilder();
    TalonConfiguration config = builder.name(name).build();
    config.addAllTalonIds(talonSet.getSelectedTalonIds());
    talonSet.setActiveTalonConfiguration(config);
    configurationsManager.getTalonProvisioner().addConfiguration(config);
    configurationsManager.save();
    terminal.writer().println(Messages.bold("saved configuration: " + name));
  }
}
