package org.strykeforce.thirdcoast.telemetry.tct.di;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import java.util.Set;
import javax.inject.Singleton;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.tct.Command;
import org.strykeforce.thirdcoast.telemetry.tct.CommandAdapter;
import org.strykeforce.thirdcoast.telemetry.tct.DioModeCommand;
import org.strykeforce.thirdcoast.telemetry.tct.MainMenu;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.QuitCommand;
import org.strykeforce.thirdcoast.telemetry.tct.ServoModeCommand;
import org.strykeforce.thirdcoast.telemetry.tct.TalonModeCommand;
import org.strykeforce.thirdcoast.telemetry.tct.dio.DioMenuComponent;
import org.strykeforce.thirdcoast.telemetry.tct.servo.ServoMenuComponent;
import org.strykeforce.thirdcoast.telemetry.tct.talon.di.TalonMenuComponent;

@Module(subcomponents = {
    TalonMenuComponent.class,
    ServoMenuComponent.class,
    DioMenuComponent.class
})
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
  public static Menu menu(@MainMenu CommandAdapter commandsAdapter, Terminal terminal) {
    Menu menu = new Menu(commandsAdapter, terminal);
    menu.setMainMenu(true);
    return menu;
  }

  @Singleton
  @Binds
  @IntoSet
  @MainMenu
  public abstract Command servoModeCommand(ServoModeCommand command);

  @Singleton
  @Binds
  @IntoSet
  @MainMenu
  public abstract Command dioModeCommand(DioModeCommand command);

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
