package org.strykeforce.thirdcoast.telemetry.tct.talon.config.lim;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import java.util.Set;
import javax.inject.Named;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.Command;
import org.strykeforce.thirdcoast.telemetry.tct.CommandAdapter;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonMenu;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;

@Module
public abstract class LimitMenuModule {

  @Provides
  @Named("TALON_CONFIG_LIM")
  public static CommandAdapter configCommandsAdapter(
      @Named("TALON_CONFIG_LIM") Set<Command> commands) {
    return new CommandAdapter("TALON_CONFIG_LIM", commands);
  }

  @Provides
  @Named("TALON_CONFIG_LIM")
  public static Menu configMenu(@Named("TALON_CONFIG_LIM") CommandAdapter commandAdapter,
      LineReader reader, TalonSet talonSet) {
    return new TalonMenu(commandAdapter, reader, talonSet);
  }

  @Binds
  @IntoSet
  @Named("TALON_CONFIG_LIM")
  public abstract Command forwardSoftLimitCommand(ForwardSoftLimitCommand command);

  @Binds
  @IntoSet
  @Named("TALON_CONFIG_LIM")
  public abstract Command reverseSoftLimitCommand(ReverseSoftLimitCommand command);

  @Binds
  @IntoSet
  @Named("TALON_CONFIG_LIM")
  public abstract Command enableForwardSoftLimitCommand(EnableForwardSoftLimitCommand command);

  @Binds
  @IntoSet
  @Named("TALON_CONFIG_LIM")
  public abstract Command enableReverseSoftLimitCommand(EnableReverseSoftLimitCommand command);

  @Binds
  @IntoSet
  @Named("TALON_CONFIG_LIM")
  public abstract Command limitSwitchEnabled(LimitSwitchEnabled command);
}
