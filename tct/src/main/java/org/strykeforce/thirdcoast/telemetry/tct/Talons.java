package org.strykeforce.thirdcoast.telemetry.tct;

import com.ctre.CANTalon;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.TalonConfiguration;
import org.strykeforce.thirdcoast.talon.TalonFactory;
import org.strykeforce.thirdcoast.talon.TalonProvisioner;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

@Singleton
public class Talons {

  final static Logger logger = LoggerFactory.getLogger(Talons.class);
  final static double MIN_BUS_VOLTAGE = 9.0;

  private final Terminal terminal;
  private final Prompts prompts;
  private final LineReader reader;
  private final TelemetryService telemetryService;
  private final TalonFactory talonFactory;
  private final TalonProvisioner talonProvisioner;
  Set<CANTalon> talons = new HashSet<>();
  Set<CANTalon> selected = new HashSet<>();

  @Inject
  public Talons(Terminal terminal, Prompts prompts, TelemetryService telemetryService,
      TalonFactory talonFactory, TalonProvisioner talonProvisioner) {
    this.terminal = terminal;
    this.prompts = prompts;
    this.telemetryService = telemetryService;
    this.talonFactory = talonFactory;
    this.talonProvisioner = talonProvisioner;
    this.reader = LineReaderBuilder.builder().terminal(terminal).build();
  }

  private void logStatus(String message) {
    terminal.writer().println(message);
    logger.info(message);
  }

  public void load() {
    logStatus("loading talons");
    try (FileConfig configs = FileConfig.of("tct.toml")) {
      configs.load();
      talonProvisioner.addConfigurations(configs);
      List<Config> configList = configs.get("TALON");
      if (configList == null) {
        logStatus("no talons available to load");
        return;
      }
      for (Config config : configList) {
        List<Integer> ids = config.get("deviceId");
        for (int i : ids) {
          String name = (String) config.getOptional(TalonConfiguration.NAME)
              .orElse(TalonProvisioner.DEFAULT_CONFIG);
          CANTalon talon = talonFactory.createTalonWithConfiguration(i, name);
          talons.add(talon);
          logger.info("adding talon with id {} and configuration {}", i, name);
        }
      }
    }
//    terminal.puts(Capability.clear_screen);
  }

  public void list() {
    terminal.writer().println("listing talons:");
    for (CANTalon talon : talons) {
      terminal.writer().printf("%d: %s selected: %b%n", talon.getDeviceID(), talon.getDescription(),
          selected.contains(talon));
    }
    terminal.writer().println();
  }

  public void select() {
    for (CANTalon talon : talons) {
      if (talon.getDeviceID() == 6) {
        selected.add(talon);
        telemetryService.register(talon);
      }
    }
    telemetryService.start();
  }

  void start() {
    terminal.writer().println("Enter motor setpoint, press <enter> to go back");
    while (true) {
      String line = null;
      try {
        line = reader.readLine("setpoint> ", prompts.rightPrompt(), (Character) null, null).trim();
      } catch (EndOfFileException | UserInterruptException e) {
        continue;
      }

      if (line.isEmpty()) {
        return;
      }
      double setpoint = 0;
      try {
        setpoint = Double.valueOf(line);
      } catch (NumberFormatException nfe) {
        terminal.writer().println("please enter a number");
        continue;
      }
      terminal.writer().printf("Setting talons to %f%n", setpoint);
      for (CANTalon talon : selected) {
        talon.set(setpoint);
      }
    }
  }
}
