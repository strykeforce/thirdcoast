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

@Module
public abstract class ConfigMenuModule {

  public final static List<String> MENU_ORDER = Arrays.asList(
      ModeCommand.NAME,
      PCommand.NAME,
      ICommand.NAME,
      DCommand.NAME,
      FCommand.NAME
  );

  @TalonConfigScope
  @Provides
  @TalonConfigMenu
  public static CommandAdapter configCommandsAdapter(@TalonConfigMenu Set<Command> commands) {
    return new CommandAdapter(commands);
  }

  @TalonConfigScope
  @Provides
  @TalonConfigMenu
  public static Menu configMenu(@TalonConfigMenu CommandAdapter commandAdapter, Terminal terminal) {
    return new Menu(commandAdapter, terminal);
  }

  @TalonConfigScope
  @Binds
  @IntoSet
  @TalonConfigMenu
  public abstract Command modeCommand(ModeCommand command);

  @TalonConfigScope
  @Binds
  @IntoSet
  @TalonConfigMenu
  public abstract Command PCommand(PCommand command);

  @TalonConfigScope
  @Binds
  @IntoSet
  @TalonConfigMenu
  public abstract Command ICommand(ICommand command);

  @TalonConfigScope
  @Binds
  @IntoSet
  @TalonConfigMenu
  public abstract Command DCommand(DCommand command);

  @TalonConfigScope
  @Binds
  @IntoSet
  @TalonConfigMenu
  public abstract Command FCommand(FCommand command);
}
