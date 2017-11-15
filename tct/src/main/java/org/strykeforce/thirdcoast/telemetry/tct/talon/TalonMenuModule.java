package org.strykeforce.thirdcoast.telemetry.tct.talon;

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
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.ConfigMenuModule;

@Module(includes = ConfigMenuModule.class)
public abstract class TalonMenuModule {

  @Provides
  @Named("TALON")
  public static CommandAdapter talonCommandsAdapter(@Named("TALON") Set<Command> commands) {
    return new CommandAdapter("TALON", commands);
  }

  @Provides
  @Named("TALON")
  public static Menu talonMenu(@Named("TALON") CommandAdapter commandAdapter, LineReader reader,
      TalonSet talonSet) {
    return new TalonMenu(commandAdapter, reader, talonSet);
  }

  @Binds
  @IntoSet
  @Named("TALON")
  public abstract Command configCommand(ConfigModeCommand command);

  @Binds
  @IntoSet
  @Named("TALON")
  public abstract Command listCommand(ListCommand command);

  @Binds
  @IntoSet
  @Named("TALON")
  public abstract Command selectCommand(SelectCommand command);

  @Binds
  @IntoSet
  @Named("TALON")
  public abstract Command loadCommand(LoadConfigsCommand command);

  @Binds
  @IntoSet
  @Named("TALON")
  public abstract Command inspectCommand(InspectCommand command);

  @Binds
  @IntoSet
  @Named("TALON")
  public abstract Command runCommand(RunCommand command);

  @Binds
  @IntoSet
  @Named("TALON")
  public abstract Command saveConfigCommand(SaveConfigCommand command);


}
