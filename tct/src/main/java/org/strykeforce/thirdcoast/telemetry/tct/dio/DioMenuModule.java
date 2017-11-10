package org.strykeforce.thirdcoast.telemetry.tct.dio;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import java.util.Set;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.Command;
import org.strykeforce.thirdcoast.telemetry.tct.CommandAdapter;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.QuitCommand;
import org.strykeforce.thirdcoast.telemetry.tct.di.ModeScoped;

@Module
public abstract class DioMenuModule {

  @ModeScoped
  @Provides
  @DioMenu
  static CommandAdapter talonCommandsAdapter(@DioMenu Set<Command> commands) {
    return new CommandAdapter(commands);
  }

  @ModeScoped
  @Provides
  @DioMenu
  static Menu talonMenu(@DioMenu CommandAdapter commandAdapter, LineReader reader) {
    return new Menu(commandAdapter, reader);
  }

  @ModeScoped
  @Binds
  @IntoSet
  @DioMenu
  public abstract Command configCommand(QuitCommand command);

}
