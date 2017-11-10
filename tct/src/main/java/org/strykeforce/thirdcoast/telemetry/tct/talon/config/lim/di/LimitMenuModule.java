package org.strykeforce.thirdcoast.telemetry.tct.talon.config.lim.di;

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
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonModeMenu;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.lim.ForwardSoftLimitCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.lim.LimitSwitchEnabled;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.lim.ReverseSoftLimitCommand;

@Module
public abstract class LimitMenuModule {

  @SubConfigScoped
  @Provides
  @LimitMenu
  public static CommandAdapter configCommandsAdapter(@LimitMenu Set<Command> commands) {
    return new CommandAdapter("TALON_CONFIG_LIM", commands);
  }

  @SubConfigScoped
  @Provides
  @LimitMenu
  public static Menu configMenu(@LimitMenu CommandAdapter commandAdapter, LineReader reader,
      TalonSet talonSet) {
    return new TalonModeMenu(commandAdapter, reader, talonSet);
  }

  @SubConfigScoped
  @Binds
  @IntoSet
  @LimitMenu
  public abstract Command forwardSoftLimitCommand(ForwardSoftLimitCommand command);

  @SubConfigScoped
  @Binds
  @IntoSet
  @LimitMenu
  public abstract Command reverseSoftLimitCommand(ReverseSoftLimitCommand command);

  @SubConfigScoped
  @Binds
  @IntoSet
  @LimitMenu
  public abstract Command limitSwitchEnabled(LimitSwitchEnabled command);
}
