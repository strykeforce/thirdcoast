package org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl;

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
import org.strykeforce.thirdcoast.telemetry.tct.SubConfigScope;

@Module
public abstract class ClosedLoopMenuModule {

  public final static List<String> MENU_ORDER = Arrays.asList(
      PCommand.NAME,
      ICommand.NAME,
      DCommand.NAME,
      FCommand.NAME,
      IZoneCommand.NAME,
      AllowableClosedLoopErrorCommand.NAME
      );

  @SubConfigScope
  @Provides
  @ClosedLoopMenu
  public static CommandAdapter configCommandsAdapter(@ClosedLoopMenu Set<Command> commands) {
    return new CommandAdapter(commands);
  }

  @SubConfigScope
  @Provides
  @ClosedLoopMenu
  public static Menu configMenu(@ClosedLoopMenu CommandAdapter commandAdapter, Terminal terminal) {
    return new Menu(commandAdapter, terminal);
  }

  @SubConfigScope
  @Binds
  @IntoSet
  @ClosedLoopMenu
  public abstract Command pCommand(PCommand command);

  @SubConfigScope
  @Binds
  @IntoSet
  @ClosedLoopMenu
  public abstract Command iCommand(ICommand command);

  @SubConfigScope
  @Binds
  @IntoSet
  @ClosedLoopMenu
  public abstract Command dCommand(DCommand command);

  @SubConfigScope
  @Binds
  @IntoSet
  @ClosedLoopMenu
  public abstract Command fCommand(FCommand command);

  @SubConfigScope
  @Binds
  @IntoSet
  @ClosedLoopMenu
  public abstract Command iZoneCommand(IZoneCommand command);

  @SubConfigScope
  @Binds
  @IntoSet
  @ClosedLoopMenu
  public abstract Command allowableClosedLoopErrorCommand(AllowableClosedLoopErrorCommand command);
}
