package org.strykeforce.thirdcoast.telemetry.tct.di;

import com.ctre.CANTalon;
import dagger.Module;
import dagger.Provides;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Singleton;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

@Module
public abstract class TerminalModule {

  // TODO: check if needed
//  @Provides
//  @Singleton
//  public static Set<CANTalon> provideTalons() {
//    return new HashSet<>();
//  }

  @Provides
  @Singleton
  public static Terminal provideTerminal() {
    try {
      return TerminalBuilder.terminal();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Provides
  @Singleton
  public static LineReader provideLineReader(Terminal terminal) {
    return LineReaderBuilder.builder().terminal(terminal).appName("TCT").build();
  }
}
