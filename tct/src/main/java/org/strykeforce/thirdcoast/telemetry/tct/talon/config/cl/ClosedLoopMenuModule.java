package org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl;

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
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl.AllowableClosedLoopErrorCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl.DCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl.FCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl.ICommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl.IZoneCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl.PCommand;

@Module
public abstract class ClosedLoopMenuModule {

  @Provides
  @Named("TALON_CONFIG_CL")
  public static CommandAdapter configCommandsAdapter(@Named("TALON_CONFIG_CL") Set<Command> commands) {
    return new CommandAdapter("TALON_CONFIG_CL", commands);
  }

  @Provides
  @Named("TALON_CONFIG_CL")
  public static Menu configMenu(@Named("TALON_CONFIG_CL") CommandAdapter commandAdapter, LineReader reader,
                                TalonSet talonSet) {
    return new TalonMenu(commandAdapter, reader, talonSet);
  }

  @Binds
  @IntoSet
  @Named("TALON_CONFIG_CL")
  public abstract Command pCommand(PCommand command);

  @Binds
  @IntoSet
  @Named("TALON_CONFIG_CL")
  public abstract Command iCommand(ICommand command);

  @Binds
  @IntoSet
  @Named("TALON_CONFIG_CL")
  public abstract Command dCommand(DCommand command);

  @Binds
  @IntoSet
  @Named("TALON_CONFIG_CL")
  public abstract Command fCommand(FCommand command);

  @Binds
  @IntoSet
  @Named("TALON_CONFIG_CL")
  public abstract Command iZoneCommand(IZoneCommand command);

  @Binds
  @IntoSet
  @Named("TALON_CONFIG_CL")
  public abstract Command allowableClosedLoopErrorCommand(AllowableClosedLoopErrorCommand command);

  @Binds
  @IntoSet
  @Named("TALON_CONFIG_CL")
  public abstract Command motionMagicAccelerationCommand(MotionMagicAccelerationCommand command);

  @Binds
  @IntoSet
  @Named("TALON_CONFIG_CL")
  public abstract Command motionMagicCruiseVelocityCommand(MotionMagicCruiseVelocityCommand command);
}
