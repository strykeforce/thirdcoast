package org.strykeforce.thirdcoast.telemetry.tct.di;

import dagger.BindsInstance;
import dagger.Component;
import java.io.File;
import javax.inject.Singleton;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;

@Singleton
@Component(modules = {
    TerminalModule.class,
    MenuModule.class,
    TalonConfigModule.class,
    FileConfigModule.class,
})
public interface MainComponent {

  @MainMenu
  Menu menu();

  Terminal terminal();

  @Component.Builder
  interface Builder {

    @BindsInstance
    Builder configFile(File file);

    MainComponent build();
  }

}
