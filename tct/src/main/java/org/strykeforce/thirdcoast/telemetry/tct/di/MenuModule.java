package org.strykeforce.thirdcoast.telemetry.tct.di;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import java.util.Set;
import javax.inject.Named;
import javax.inject.Singleton;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.Command;
import org.strykeforce.thirdcoast.telemetry.tct.CommandAdapter;
import org.strykeforce.thirdcoast.telemetry.tct.DioModeCommand;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.QuitCommand;
import org.strykeforce.thirdcoast.telemetry.tct.ServoModeCommand;
import org.strykeforce.thirdcoast.telemetry.tct.TalonModeCommand;

@Module
public abstract class MenuModule {

  @Provides
  @Singleton
  @Named("MAIN")
  static CommandAdapter commandsAdapter(@Named("MAIN") Set<Command> commands) {
    return new CommandAdapter("MAIN", commands);
  }

  @Provides
  @Singleton
  @Named("MAIN")
  public static Menu menu(@Named("MAIN") CommandAdapter commandsAdapter, LineReader reader) {
    Menu menu = new Menu(commandsAdapter, reader);
    menu.setMainMenu(true);
    return menu;
  }

  @Binds
  @Singleton
  @IntoSet
  @Named("MAIN")
  public abstract Command servoModeCommand(ServoModeCommand command);

  @Binds
  @Singleton
  @IntoSet
  @Named("MAIN")
  public abstract Command dioModeCommand(DioModeCommand command);

  @Binds
  @Singleton
  @IntoSet
  @Named("MAIN")
  public abstract Command talonModeCommand(TalonModeCommand command);

  @Binds
  @Singleton
  @IntoSet
  @Named("MAIN")
  public abstract Command quitCommand(QuitCommand command);
}
