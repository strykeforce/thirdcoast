package org.strykeforce.thirdcoast.telemetry.tct.talon;

import com.ctre.CANTalon;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.TalonConfiguration;
import org.strykeforce.thirdcoast.talon.TalonFactory;
import org.strykeforce.thirdcoast.talon.TalonProvisioner;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.Command;
import org.strykeforce.thirdcoast.telemetry.tct.ConfigurationsManager;
import org.strykeforce.thirdcoast.telemetry.tct.di.ModeScoped;
import org.strykeforce.thirdcoast.telemetry.tct.talon.di.TalonMenuModule;

/**
 * Loads Talons from TOML configuration file named {@code tct.toml}.
 */
@ModeScoped
public class LoadConfigsCommand extends AbstractCommand {

  public final static String NAME = "Load Talons";
  final static Logger logger = LoggerFactory.getLogger(LoadConfigsCommand.class);

  private final ConfigurationsManager configurationsManager;
  private final TalonSet talonSet;
  private final TalonFactory talonFactory;
  private final Optional<Command> listCommand;


  @Inject
  public LoadConfigsCommand(ConfigurationsManager configurationsManager, TalonSet talonSet,
      LineReader reader, TalonFactory talonFactory, ListCommand listCommand) {
    super(NAME, TalonMenuModule.MENU_ORDER.indexOf(NAME), reader);
    this.configurationsManager = configurationsManager;
    this.talonSet = talonSet;
    this.talonFactory = talonFactory;
    this.listCommand = Optional.of(listCommand);
  }

  protected static String prompt() {
    return new AttributedStringBuilder()
        .style(AttributedStyle.BOLD.foreground(AttributedStyle.YELLOW))
        .append("config to load or <enter> to return> ").toAnsi();
  }

  @Override
  public void perform() {
    TalonProvisioner provisioner = configurationsManager.getTalonProvisioner();
    List<String> configNames = provisioner.getConfigurationNames().stream()
        .sorted().collect(Collectors.toList());
    for (int i = 0; i < configNames.size(); i++) {
      terminal.writer().printf("%2d - %s%n", i + 1, configNames.get(i));
    }
    String selected = null;
    while (selected == null) {
      String line = null;
      try {
        line = reader.readLine(prompt()).trim();
      } catch (EndOfFileException | UserInterruptException e) {
        break;
      }

      if (line.isEmpty()) {
        logger.info("no talon configuration selected");
        break;
      }

      // get the Talon configuration name from the menu selection
      int choice;
      try {
        choice = Integer.valueOf(line);
      } catch (NumberFormatException nfe) {
        help(configNames.size());
        continue;
      }

      if (choice < 1 || choice > configNames.size()) {
        help(configNames.size());
        continue;
      }

      // load the Talon configuration
      selected = configNames.get(choice - 1);
      TalonConfiguration configuration = provisioner.configurationFor(selected);
      talonSet.setActiveTalonConfiguration(configuration);

      // have a configuration, clear out everything and load Talons stored with it.
      talonSet.clearSelected();

      for (Integer id : configuration.getTalonIds()) {
        CANTalon talon = talonFactory.getTalonWithConfiguration(id, selected);
        talonSet.selectTalon(talon);
        logger.info("adding talon with id {} and configuration {}", id, selected);
      }
    }
    talonSet.restartTelemetryService();
  }

  /**
   * Returns {@code ListCommand}
   *
   * @return the ListCommand
   */
  @Override
  public Optional<Command> post() {
    return listCommand;
  }

  private void help(int size) {
    String msg = String.format("please enter a number between 1-%d or <enter> to return%n", size);
    terminal.writer().print(bold(msg));
  }

}

