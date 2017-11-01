package org.strykeforce.thirdcoast.telemetry.tct.talon;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import java.util.Set;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.tct.CommandAdapter;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.Command;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.ConfigMenuComponent;

@Module(subcomponents = ConfigMenuComponent.class)
public abstract class TalonMenuModule {

  @TalonScope
  @Provides
  @TalonMenu
  public static CommandAdapter talonCommandsAdapter(@TalonMenu Set<Command> commands) {
    return new CommandAdapter(commands);
  }

  @TalonScope
  @Provides
  @TalonMenu
  public static Menu talonMenu(@TalonMenu CommandAdapter commandAdapter, Terminal terminal) {
    return new Menu(commandAdapter, terminal);
  }

  @TalonScope
  @Binds
  @IntoSet
  @TalonMenu
  public abstract Command configCommand(ConfigCommand command);

  @TalonScope
  @Binds
  @IntoSet
  @TalonMenu
  public abstract Command listCommand(ListCommand command);

  @TalonScope
  @Binds
  @IntoSet
  @TalonMenu
  public abstract Command selectCommand(SelectCommand command);

  @TalonScope
  @Binds
  @IntoSet
  @TalonMenu
  public abstract Command loadCommand(LoadCommand command);

  @TalonScope
  @Binds
  @IntoSet
  @TalonMenu
  public abstract Command inspectCommand(InspectCommand command);

  @TalonScope
  @Binds
  @IntoSet
  @TalonMenu
  public abstract Command runCommand(RunCommand command);
}
