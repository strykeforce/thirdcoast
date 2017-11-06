package org.strykeforce.thirdcoast.telemetry.tct.dio;

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
import org.strykeforce.thirdcoast.telemetry.tct.di.ModeScoped;
import org.strykeforce.thirdcoast.telemetry.tct.QuitCommand;

@Module
public abstract class DioMenuModule {

  public static final List<String> MENU_ORDER = Arrays.asList(
      QuitCommand.NAME
  );

  @ModeScoped
  @Provides
  @DioMenu
  public static CommandAdapter talonCommandsAdapter(@DioMenu Set<Command> commands) {
    return new CommandAdapter(commands);
  }

  @ModeScoped
  @Provides
  @DioMenu
  public static Menu talonMenu(@DioMenu CommandAdapter commandAdapter, Terminal terminal) {
    return new Menu(commandAdapter, terminal);
  }

  @ModeScoped
  @Binds
  @IntoSet
  @DioMenu
  public abstract Command configCommand(QuitCommand command);

}
