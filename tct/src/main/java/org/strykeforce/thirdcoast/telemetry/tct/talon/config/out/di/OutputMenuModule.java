package org.strykeforce.thirdcoast.telemetry.tct.talon.config.out.di;

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
import org.strykeforce.thirdcoast.telemetry.tct.di.SubConfigScoped;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.out.ClosedLoopRampRateCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.out.CurrentLimitCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.out.NominalOutputVoltageCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.out.OutputMenu;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.out.PeakOutputVoltageCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.out.VoltageRampRateCommand;

@Module
public abstract class OutputMenuModule {

  public final static List<String> MENU_ORDER = Arrays.asList(
      VoltageRampRateCommand.NAME,
      PeakOutputVoltageCommand.NAME,
      ClosedLoopRampRateCommand.NAME,
      NominalOutputVoltageCommand.NAME,
      CurrentLimitCommand.NAME
  );

  @SubConfigScoped
  @Provides
  @OutputMenu
  public static CommandAdapter configCommandsAdapter(@OutputMenu Set<Command> commands) {
    return new CommandAdapter(commands);
  }

  @SubConfigScoped
  @Provides
  @OutputMenu
  public static Menu configMenu(@OutputMenu CommandAdapter commandAdapter, Terminal terminal) {
    return new Menu(commandAdapter, terminal);
  }

  @SubConfigScoped
  @Binds
  @IntoSet
  @OutputMenu
  public abstract Command voltageRampRateCommand(VoltageRampRateCommand command);

  @SubConfigScoped
  @Binds
  @IntoSet
  @OutputMenu
  public abstract Command peakOutputVoltageCommand(PeakOutputVoltageCommand command);

  @SubConfigScoped
  @Binds
  @IntoSet
  @OutputMenu
  public abstract Command closedLoopRampRateCommand(ClosedLoopRampRateCommand command);

  @SubConfigScoped
  @Binds
  @IntoSet
  @OutputMenu
  public abstract Command nominalOutputVoltageCommand(NominalOutputVoltageCommand command);

  @SubConfigScoped
  @Binds
  @IntoSet
  @OutputMenu
  public abstract Command currentLimitCommand(CurrentLimitCommand command);

}
