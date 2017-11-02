package org.strykeforce.thirdcoast.telemetry.tct.talon.config.limit;

import dagger.Subcomponent;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.TalonSubConfigScope;

@TalonSubConfigScope
@Subcomponent(modules = LimitMenuModule.class)
public interface LimitMenuComponent {

  @LimitMenu
  Menu menu();

  @Subcomponent.Builder
  interface Builder {

    LimitMenuComponent build();
  }

}
