package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

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
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl.ClosedLoopMenuComponent;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.EncoderMenuComponent;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.lim.LimitMenuComponent;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.out.OutputMenuComponent;

@Module(subcomponents = {
    ClosedLoopMenuComponent.class,
    OutputMenuComponent.class,
    EncoderMenuComponent.class,
    LimitMenuComponent.class,
})
public abstract class ConfigMenuModule {

  public final static List<String> MENU_ORDER = Arrays.asList(
      SelectOperatingModeCommand.NAME,
      ClosedLoopConfigCommand.NAME,
      OutputConfigCommand.NAME,
      EncoderConfigCommand.NAME,
      LimitConfigCommand.NAME
  );

  @ConfigScope
  @Provides
  @TalonConfigMenu
  public static CommandAdapter configCommandsAdapter(@TalonConfigMenu Set<Command> commands) {
    return new CommandAdapter(commands);
  }

  @ConfigScope
  @Provides
  @TalonConfigMenu
  public static Menu configMenu(@TalonConfigMenu CommandAdapter commandAdapter, Terminal terminal) {
    return new Menu(commandAdapter, terminal);
  }

  @ConfigScope
  @Binds
  @IntoSet
  @TalonConfigMenu
  public abstract Command selectOperatingModeCommand(SelectOperatingModeCommand command);

  @ConfigScope
  @Binds
  @IntoSet
  @TalonConfigMenu
  public abstract Command closedLoopConfigCommand(ClosedLoopConfigCommand command);

  @ConfigScope
  @Binds
  @IntoSet
  @TalonConfigMenu
  public abstract Command voltageRampRateCommand(OutputConfigCommand command);

  @ConfigScope
  @Binds
  @IntoSet
  @TalonConfigMenu
  public abstract Command encoderConfigCommand(EncoderConfigCommand command);

  @ConfigScope
  @Binds
  @IntoSet
  @TalonConfigMenu
  public abstract Command limitConfigCommand(LimitConfigCommand command);

}
