package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

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
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl.ClosedLoopMenuModule;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.EncoderMenuModule;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.lim.LimitMenuModule;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.out.OutputMenuModule;

@Module(includes = {
    ClosedLoopMenuModule.class,
    EncoderMenuModule.class,
    LimitMenuModule.class,
    OutputMenuModule.class,
})
public abstract class ConfigMenuModule {

  @Provides
  @Named("TALON_CONFIG")
  public static CommandAdapter configCommandsAdapter(@Named("TALON_CONFIG") Set<Command> commands) {
    return new CommandAdapter("TALON_CONFIG", commands);
  }

  @Provides
  @Named("TALON_CONFIG")
  public static Menu configMenu(@Named("TALON_CONFIG") CommandAdapter commandAdapter,
      LineReader reader,
      TalonSet talonSet) {
    return new TalonMenu(commandAdapter, reader, talonSet);
  }

  @Binds
  @IntoSet
  @Named("TALON_CONFIG")
  public abstract Command selectOperatingModeCommand(SelectOperatingModeCommand command);

  @Binds
  @IntoSet
  @Named("TALON_CONFIG")
  public abstract Command closedLoopConfigCommand(ClosedLoopConfigCommand command);

  @Binds
  @IntoSet
  @Named("TALON_CONFIG")
  public abstract Command voltageRampRateCommand(OutputConfigCommand command);

  @Binds
  @IntoSet
  @Named("TALON_CONFIG")
  public abstract Command encoderConfigCommand(EncoderConfigCommand command);

  @Binds
  @IntoSet
  @Named("TALON_CONFIG")
  public abstract Command limitConfigCommand(LimitConfigCommand command);

}
