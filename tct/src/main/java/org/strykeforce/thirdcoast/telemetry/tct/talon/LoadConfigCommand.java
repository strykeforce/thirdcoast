package org.strykeforce.thirdcoast.telemetry.tct.talon;

import com.ctre.CANTalon;
import com.moandjiezana.toml.Toml;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.TalonConfigurationBuilder;
import org.strykeforce.thirdcoast.talon.TalonFactory;
import org.strykeforce.thirdcoast.talon.TalonProvisioner;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.Command;
import org.strykeforce.thirdcoast.telemetry.tct.ConfigFile;
import org.strykeforce.thirdcoast.telemetry.tct.di.ModeScoped;
import org.strykeforce.thirdcoast.telemetry.tct.talon.di.TalonMenuModule;

/**
 * Loads Talons from TOML configuration file named {@code tct.toml}.
 */
@ModeScoped
public class LoadConfigCommand extends AbstractCommand {

  public final static String NAME = "Load Talons";
  final static Logger logger = LoggerFactory.getLogger(LoadConfigCommand.class);

  private final ConfigFile configFile;
  private final TalonSet talonSet;
  private final TalonProvisioner talonProvisioner;
  private final TalonFactory talonFactory;
  private final TelemetryService telemetryService;
  private final Optional<Command> listCommand;


  @Inject
  public LoadConfigCommand(ConfigFile configFile, TalonSet talonSet, LineReader reader,
      TalonProvisioner talonProvisioner, TalonFactory talonFactory,
      TelemetryService telemetryService, ListCommand listCommand) {
    super(NAME, TalonMenuModule.MENU_ORDER.indexOf(NAME), reader);
    this.configFile = configFile;
    this.talonSet = talonSet;
    this.talonProvisioner = talonProvisioner;
    this.talonFactory = talonFactory;
    this.telemetryService = telemetryService;
    this.listCommand = Optional.of(listCommand);
  }

  @Override
  public void perform() {
    Toml configs = configFile.load();
    talonProvisioner.addConfigurations(configs);
    List<Toml> configList = configs.getTables("TALON");
    if (configList == null) {
      String message = "no talons available to load";
      logger.error(message);
      terminal.writer().println("no talons available to load");
      return;
    }
    telemetryService.stop();
    telemetryService.clear();
    talonSet.all.clear();
    for (Toml config : configList) {
      List<Long> ids = config.getList("deviceId"); // FIXME: NPE if missing, still?
      for (long i : ids) {
        String name = (String) config.getString(TalonConfigurationBuilder.NAME);
        CANTalon talon = talonFactory.createTalonWithConfiguration((int) i, name);
        talonSet.all.add(talon);
        telemetryService.register(talon);
        logger.info("adding talon with id {} and configuration {}", i, name);
      }
    }
    telemetryService.start();
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
