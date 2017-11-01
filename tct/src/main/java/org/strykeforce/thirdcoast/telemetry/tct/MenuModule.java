package org.strykeforce.thirdcoast.telemetry.tct;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import java.util.Set;
import javax.inject.Singleton;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonMenuComponent;

@Module(subcomponents = TalonMenuComponent.class)
public abstract class MenuModule {

  @Singleton
  @Provides
  @MainMenu
  public static CommandAdapter topCommandsAdapter(@MainMenu Set<Command> commands) {
    return new CommandAdapter(commands);
  }

  @Singleton
  @Provides
  @MainMenu
  public static Menu mainMenu(@MainMenu CommandAdapter commandsAdapter, Terminal terminal) {
    return new Menu(commandsAdapter, terminal);
  }

  @Singleton
  @Binds
  @IntoSet
  @MainMenu
  public abstract Command talonModeCommand(TalonModeCommand command);

  @Singleton
  @Binds
  @IntoSet
  @MainMenu
  public abstract Command quitCommand(QuitCommand command);

}
