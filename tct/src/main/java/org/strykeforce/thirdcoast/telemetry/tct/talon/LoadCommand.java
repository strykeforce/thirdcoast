package org.strykeforce.thirdcoast.telemetry.tct.talon;

import com.ctre.CANTalon;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.TalonConfiguration;
import org.strykeforce.thirdcoast.talon.TalonFactory;
import org.strykeforce.thirdcoast.talon.TalonProvisioner;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.Command;
import org.strykeforce.thirdcoast.telemetry.tct.ModeScope;

/**
 * Loads Talons from TOML configuration file named {@code tct.toml}.
 */
@ModeScope
public class LoadCommand extends AbstractCommand {

  public final static String NAME = "Load Talons";
  final static Logger logger = LoggerFactory.getLogger(LoadCommand.class);

  private final TalonSet talonSet;
  private final TalonProvisioner talonProvisioner;
  private final TalonFactory talonFactory;
  private final TelemetryService telemetryService;
  private final Optional<Command> listCommand;


  @Inject
  public LoadCommand(TalonSet talonSet, LineReader reader, TalonProvisioner talonProvisioner,
      TalonFactory talonFactory, TelemetryService telemetryService, ListCommand listCommand) {
    super(NAME, TalonMenuModule.MENU_ORDER.indexOf(NAME), reader);
    this.talonSet = talonSet;
    this.talonProvisioner = talonProvisioner;
    this.talonFactory = talonFactory;
    this.telemetryService = telemetryService;
    this.listCommand = Optional.of(listCommand);
  }

  @Override
  public void perform() {
    try (FileConfig configs = FileConfig.of("tct.toml")) {
      configs.load();
      talonProvisioner.addConfigurations(configs);
      List<Config> configList = configs.get("TALON");
      if (configList == null) {
        String message = "no talons available to load";
        logger.error(message);
        terminal.writer().println("no talons available to load");
        return;
      }
      telemetryService.stop();
      telemetryService.clear();
      talonSet.all.clear();
      for (Config config : configList) {
        List<Integer> ids = config.get("deviceId"); // FIXME: NPE if missing
        for (int i : ids) {
          String name = (String) config.getOptional(TalonConfiguration.NAME)
              .orElse(TalonProvisioner.DEFAULT_CONFIG);
          CANTalon talon = talonFactory.createTalonWithConfiguration(i, name);
          talonSet.all.add(talon);
          telemetryService.register(talon);
          logger.info("adding talon with id {} and configuration {}", i, name);
        }
      }
      telemetryService.start();
    }
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
}
