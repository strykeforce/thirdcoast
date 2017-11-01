package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import dagger.Subcomponent;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonScope;

@TalonConfigScope
@Subcomponent(modules = ConfigMenuModule.class)
public interface ConfigMenuComponent {

  @TalonConfigMenu
  Menu menu();

  @Subcomponent.Builder
  interface Builder {
    ConfigMenuComponent build();
  }

}
