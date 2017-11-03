package org.strykeforce.thirdcoast.telemetry.tct.talon.config.voltage;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.tct.Command;
import org.strykeforce.thirdcoast.telemetry.tct.CommandAdapter;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.SubConfigScope;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.VoltageRampRateCommand;

@Module
public abstract class VoltageMenuModule {

  public final static List<String> MENU_ORDER = Arrays.asList(
      VoltageRampRateCommand.NAME,
      PeakOutputVoltageCommand.NAME,
      ClosedLoopRampRateCommand.NAME,
      NominalOutputVoltageCommand.NAME
  );

  @SubConfigScope
  @Provides
  @VoltageMenu
  public static CommandAdapter configCommandsAdapter(@VoltageMenu Set<Command> commands) {
    return new CommandAdapter(commands);
  }

  @SubConfigScope
  @Provides
  @VoltageMenu
  public static Menu configMenu(@VoltageMenu CommandAdapter commandAdapter, Terminal terminal) {
    return new Menu(commandAdapter, terminal);
  }

  @SubConfigScope
  @Binds
  @IntoSet
  @VoltageMenu
  public abstract Command voltageRampRateCommand(VoltageRampRateCommand command);

  @SubConfigScope
  @Binds
  @IntoSet
  @VoltageMenu
  public abstract Command peakOutputVoltageCommand(PeakOutputVoltageCommand command);

  @SubConfigScope
  @Binds
  @IntoSet
  @VoltageMenu
  public abstract Command closedLoopRampRateCommand(ClosedLoopRampRateCommand command);

  @SubConfigScope
  @Binds
  @IntoSet
  @VoltageMenu
  public abstract Command nominalOutputVoltageCommand(NominalOutputVoltageCommand command);

}
