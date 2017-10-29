package org.strykeforce.thirdcoast.telemetry.tct;

import com.ctre.CANTalon;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import dagger.BindsInstance;
import dagger.Component;
import java.util.Set;
import javax.inject.Singleton;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

@Singleton
@Component(modules = {
    TerminalModule.class,
})
public interface MainComponent {

  Menu menu();

  Set<CANTalon> talons();

  Terminal terminal();

  TelemetryService telemetryService();

  @Component.Builder
  interface Builder {

    @BindsInstance
    Builder toml(UnmodifiableConfig config);

    MainComponent build();
  }

}
