package org.strykeforce.thirdcoast.telemetry.tct.di;

import dagger.BindsInstance;
import dagger.Component;
import java.io.File;
import javax.inject.Named;
import javax.inject.Singleton;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.dio.DioMenuModule;
import org.strykeforce.thirdcoast.telemetry.tct.servo.ServoMenuModule;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonMenuModule;

@Singleton
@Component(
  modules = {
    TerminalModule.class,
    MenuModule.class,
    ServoMenuModule.class,
    DioMenuModule.class,
    TalonMenuModule.class,
  }
)
public interface MainComponent {

  @Named("MAIN")
  Menu menu();

  Terminal terminal();

  @Component.Builder
  interface Builder {

    @BindsInstance
    Builder configFile(File file);

    MainComponent build();
  }
}
