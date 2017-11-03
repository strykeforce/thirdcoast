package org.strykeforce.thirdcoast.telemetry.tct;

import dagger.Component;
import javax.inject.Singleton;
import org.jline.terminal.Terminal;

@Singleton
@Component(modules = {
    TerminalModule.class,
    MenuModule.class,
    TalonConfigModule.class,
})
public interface MainComponent {

  @MainMenu
  Menu menu();

  Terminal terminal();

  @Component.Builder
  interface Builder {

    MainComponent build();
  }

}
