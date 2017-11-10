package org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl.di;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import java.util.Set;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.Command;
import org.strykeforce.thirdcoast.telemetry.tct.CommandAdapter;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.di.SubConfigScoped;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl.AllowableClosedLoopErrorCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl.ClosedLoopMenu;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl.DCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl.FCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl.ICommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl.IZoneCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl.PCommand;

@Module
public abstract class ClosedLoopMenuModule {

  @SubConfigScoped
  @Provides
  @ClosedLoopMenu
  public static CommandAdapter configCommandsAdapter(@ClosedLoopMenu Set<Command> commands) {
    return new CommandAdapter("TALON_CONFIG_CL", commands);
  }

  @SubConfigScoped
  @Provides
  @ClosedLoopMenu
  public static Menu configMenu(@ClosedLoopMenu CommandAdapter commandAdapter, LineReader reader) {
    return new Menu(commandAdapter, reader);
  }

  @SubConfigScoped
  @Binds
  @IntoSet
  @ClosedLoopMenu
  public abstract Command pCommand(PCommand command);

  @SubConfigScoped
  @Binds
  @IntoSet
  @ClosedLoopMenu
  public abstract Command iCommand(ICommand command);

  @SubConfigScoped
  @Binds
  @IntoSet
  @ClosedLoopMenu
  public abstract Command dCommand(DCommand command);

  @SubConfigScoped
  @Binds
  @IntoSet
  @ClosedLoopMenu
  public abstract Command fCommand(FCommand command);

  @SubConfigScoped
  @Binds
  @IntoSet
  @ClosedLoopMenu
  public abstract Command iZoneCommand(IZoneCommand command);

  @SubConfigScoped
  @Binds
  @IntoSet
  @ClosedLoopMenu
  public abstract Command allowableClosedLoopErrorCommand(AllowableClosedLoopErrorCommand command);
}
