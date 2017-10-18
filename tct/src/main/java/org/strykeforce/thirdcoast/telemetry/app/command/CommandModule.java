package org.strykeforce.thirdcoast.telemetry.app.command;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import java.io.IOException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

@Module
public abstract class CommandModule {

  @Binds
  abstract Command mainCommand(MainCommand command);

  @Provides
  static Terminal provideTerminal() {
    try {
      return TerminalBuilder.terminal();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
