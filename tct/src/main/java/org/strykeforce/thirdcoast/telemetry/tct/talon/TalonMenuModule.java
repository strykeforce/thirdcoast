package org.strykeforce.thirdcoast.telemetry.tct.talon;

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
import org.strykeforce.thirdcoast.telemetry.tct.ModeScope;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.ConfigMenuComponent;

@Module(subcomponents = ConfigMenuComponent.class)
public abstract class TalonMenuModule {

  public static final List<String> MENU_ORDER = Arrays.asList(
      LoadCommand.NAME,
      SelectCommand.NAME,
      ListCommand.NAME,
      ConfigModeCommand.NAME,
      InspectCommand.NAME,
      RunCommand.NAME
  );

  @ModeScope
  @Provides
  @TalonMenu
  public static CommandAdapter talonCommandsAdapter(@TalonMenu Set<Command> commands) {
    return new CommandAdapter(commands);
  }

  @ModeScope
  @Provides
  @TalonMenu
  public static Menu talonMenu(@TalonMenu CommandAdapter commandAdapter, Terminal terminal) {
    return new Menu(commandAdapter, terminal);
  }

  @ModeScope
  @Binds
  @IntoSet
  @TalonMenu
  public abstract Command configCommand(ConfigModeCommand command);

  @ModeScope
  @Binds
  @IntoSet
  @TalonMenu
  public abstract Command listCommand(ListCommand command);

  @ModeScope
  @Binds
  @IntoSet
  @TalonMenu
  public abstract Command selectCommand(SelectCommand command);

  @ModeScope
  @Binds
  @IntoSet
  @TalonMenu
  public abstract Command loadCommand(LoadCommand command);

  @ModeScope
  @Binds
  @IntoSet
  @TalonMenu
  public abstract Command inspectCommand(InspectCommand command);

  @ModeScope
  @Binds
  @IntoSet
  @TalonMenu
  public abstract Command runCommand(RunCommand command);
}
