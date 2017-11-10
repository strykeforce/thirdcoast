package org.strykeforce.thirdcoast.telemetry.tct.servo;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.Command;
import org.strykeforce.thirdcoast.telemetry.tct.CommandAdapter;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.QuitCommand;
import org.strykeforce.thirdcoast.telemetry.tct.di.ModeScoped;

@Module
public abstract class ServoMenuModule {

  public static final List<String> MENU_ORDER = Collections.singletonList(
      QuitCommand.NAME
  );

  @ModeScoped
  @Provides
  @ServoMenu
  static CommandAdapter talonCommandsAdapter(@ServoMenu Set<Command> commands) {
    return new CommandAdapter(commands);
  }

  @ModeScoped
  @Provides
  @ServoMenu
  static Menu talonMenu(@ServoMenu CommandAdapter commandAdapter, LineReader reader) {
    return new Menu(commandAdapter, reader);
  }

  @ModeScoped
  @Binds
  @IntoSet
  @ServoMenu
  public abstract Command configCommand(QuitCommand command);

}
