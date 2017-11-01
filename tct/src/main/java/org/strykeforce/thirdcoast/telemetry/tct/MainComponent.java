package org.strykeforce.thirdcoast.telemetry.tct;

import com.ctre.CANTalon;
import dagger.Component;
import java.util.Set;
import javax.inject.Singleton;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

@Singleton
@Component(modules = {
    TerminalModule.class,
    MenuModule.class,
    TalonConfigModule.class,
})
public interface MainComponent {

  @MainMenu
  Menu menu();

  @Component.Builder
  interface Builder {

    MainComponent build();
  }

}
