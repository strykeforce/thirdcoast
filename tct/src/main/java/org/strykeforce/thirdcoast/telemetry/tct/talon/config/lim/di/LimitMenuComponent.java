package org.strykeforce.thirdcoast.telemetry.tct.talon.config.lim.di;

import dagger.Subcomponent;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.di.SubConfigScoped;

@SubConfigScoped
@Subcomponent(modules = LimitMenuModule.class)
public interface LimitMenuComponent {

  @LimitMenu
  Menu menu();

  @Subcomponent.Builder
  interface Builder {

    LimitMenuComponent build();
  }

}
