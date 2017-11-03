package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import dagger.Subcomponent;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;

@ConfigScope
@Subcomponent(modules = ConfigMenuModule.class)
public interface ConfigMenuComponent {

  @TalonConfigMenu
  Menu menu();

  @Subcomponent.Builder
  interface Builder {
    ConfigMenuComponent build();
  }

}
