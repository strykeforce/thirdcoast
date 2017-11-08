package org.strykeforce.thirdcoast.telemetry.tct.talon;

import com.ctre.CANTalon;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.talon.TalonConfigurationBuilder;
import org.strykeforce.thirdcoast.talon.TalonProvisioner;
import org.strykeforce.thirdcoast.telemetry.tct.ConfigFile;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractTalonConfigCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.di.TalonMenuModule;

public class SaveConfigCommand extends AbstractTalonConfigCommand {

  public final static String NAME = "Save Configuration";
  private final ConfigFile configFile;

  @Inject
  public SaveConfigCommand(LineReader reader, TalonSet talonSet, ConfigFile configFile) {
    super(NAME, TalonMenuModule.MENU_ORDER.indexOf(NAME), reader, talonSet);
    this.configFile = configFile;
  }

  @Override
  public void perform() {
//    Config config = Config.copy(talonSet.talonConfigurationBuilder().getConfig());
//    config.add("deviceId", selectedTalonIds());
//    String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
//    config.set(TalonConfigurationBuilder.NAME, timeStamp);
//    configFile.save(config);
  }

  private List<Integer> selectedTalonIds() {
    return talonSet.selected().stream().map(CANTalon::getDeviceID).collect(Collectors.toList());
  }
}
