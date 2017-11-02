package org.strykeforce.thirdcoast.telemetry.tct.talon.config.limit;

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
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.TalonSubConfigScope;

@Module
public abstract class LimitMenuModule {

  public final static List<String> MENU_ORDER = Arrays.asList(
      ForwardSoftLimitCommand.NAME
  );

  @TalonSubConfigScope
  @Provides
  @LimitMenu
  public static CommandAdapter configCommandsAdapter(@LimitMenu Set<Command> commands) {
    return new CommandAdapter(commands);
  }

  @TalonSubConfigScope
  @Provides
  @LimitMenu
  public static Menu configMenu(@LimitMenu CommandAdapter commandAdapter, Terminal terminal) {
    return new Menu(commandAdapter, terminal);
  }

   @TalonSubConfigScope
   @Binds
   @IntoSet
   @LimitMenu
   public abstract Command forwardSoftLimitCommand(ForwardSoftLimitCommand command);
}
