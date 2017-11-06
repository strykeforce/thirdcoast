package org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl.di;

import dagger.Subcomponent;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.di.SubConfigScoped;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl.ClosedLoopMenu;

@SubConfigScoped
@Subcomponent(modules = ClosedLoopMenuModule.class)
public interface ClosedLoopMenuComponent {

  @ClosedLoopMenu
  Menu menu();

  @Subcomponent.Builder
  interface Builder {

    ClosedLoopMenuComponent build();
  }

}
