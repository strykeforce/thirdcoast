package org.strykeforce.thirdcoast.telemetry.tct.di;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import java.util.Set;
import javax.inject.Singleton;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.Command;
import org.strykeforce.thirdcoast.telemetry.tct.CommandAdapter;
import org.strykeforce.thirdcoast.telemetry.tct.DioModeCommand;
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

  @Provides
  @Singleton
  @MainMenu
  static CommandAdapter topCommandsAdapter(@MainMenu Set<Command> commands) {
    return new CommandAdapter("MAIN", commands);
  }

  @Provides
  @Singleton
  @MainMenu
  public static Menu menu(@MainMenu CommandAdapter commandsAdapter, LineReader reader) {
    Menu menu = new Menu(commandsAdapter, reader);
    menu.setMainMenu(true);
    return menu;
  }

  @Binds
  @Singleton
  @IntoSet
  @MainMenu
  public abstract Command servoModeCommand(ServoModeCommand command);

  @Binds
  @Singleton
  @IntoSet
  @MainMenu
  public abstract Command dioModeCommand(DioModeCommand command);

  @Binds
  @Singleton
  @IntoSet
  @MainMenu
  public abstract Command talonModeCommand(TalonModeCommand command);

  @Binds
  @Singleton
  @IntoSet
  @MainMenu
  public abstract Command quitCommand(QuitCommand command);

}
