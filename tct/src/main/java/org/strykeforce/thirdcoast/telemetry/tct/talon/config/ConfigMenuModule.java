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
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.EncoderMenuComponent;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.limit.LimitMenuComponent;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.voltage.VoltageMenuComponent;

@Module(subcomponents = {
    VoltageMenuComponent.class,
    EncoderMenuComponent.class,
    LimitMenuComponent.class,
})
public abstract class ConfigMenuModule {

  public final static List<String> MENU_ORDER = Arrays.asList(
      ModeCommand.NAME,
      PCommand.NAME,
      ICommand.NAME,
      DCommand.NAME,
      FCommand.NAME,
      IZoneCommand.NAME,
      AllowableClosedLoopErrorCommand.NAME,
      VoltageConfigCommand.NAME,
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
  public abstract Command modeCommand(ModeCommand command);

  @ConfigScope
  @Binds
  @IntoSet
  @TalonConfigMenu
  public abstract Command pCommand(PCommand command);

  @ConfigScope
  @Binds
  @IntoSet
  @TalonConfigMenu
  public abstract Command iCommand(ICommand command);

  @ConfigScope
  @Binds
  @IntoSet
  @TalonConfigMenu
  public abstract Command dCommand(DCommand command);

  @ConfigScope
  @Binds
  @IntoSet
  @TalonConfigMenu
  public abstract Command fCommand(FCommand command);

  @ConfigScope
  @Binds
  @IntoSet
  @TalonConfigMenu
  public abstract Command iZoneCommand(IZoneCommand command);

  @ConfigScope
  @Binds
  @IntoSet
  @TalonConfigMenu
  public abstract Command allowableClosedLoopErrorCommand(AllowableClosedLoopErrorCommand command);

  @ConfigScope
  @Binds
  @IntoSet
  @TalonConfigMenu
  public abstract Command voltageRampRateCommand(VoltageConfigCommand command);

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
