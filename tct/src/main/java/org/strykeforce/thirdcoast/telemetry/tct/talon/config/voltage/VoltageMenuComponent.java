package org.strykeforce.thirdcoast.telemetry.tct.talon.config.voltage;

import dagger.Subcomponent;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.TalonSubConfigScope;

@TalonSubConfigScope
@Subcomponent(modules = VoltageMenuModule.class)
public interface VoltageMenuComponent {

  @VoltageMenu
  Menu menu();

  @Subcomponent.Builder
  interface Builder {

    VoltageMenuComponent build();
  }

}
