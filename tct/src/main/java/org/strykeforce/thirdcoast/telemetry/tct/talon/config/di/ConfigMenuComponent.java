package org.strykeforce.thirdcoast.telemetry.tct.talon.config.di;

import dagger.Subcomponent;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.di.ConfigScoped;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.TalonConfigMenu;

@ConfigScoped
@Subcomponent(modules = ConfigMenuModule.class)
public interface ConfigMenuComponent {

  @TalonConfigMenu
  Menu menu();

  @Subcomponent.Builder
  interface Builder {
    ConfigMenuComponent build();
  }

}
