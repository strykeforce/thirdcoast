package org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl;

import dagger.Subcomponent;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.SubConfigScope;

@SubConfigScope
@Subcomponent(modules = ClosedLoopMenuModule.class)
public interface ClosedLoopMenuComponent {

  @ClosedLoopMenu
  Menu menu();

  @Subcomponent.Builder
  interface Builder {

    ClosedLoopMenuComponent build();
  }

}
