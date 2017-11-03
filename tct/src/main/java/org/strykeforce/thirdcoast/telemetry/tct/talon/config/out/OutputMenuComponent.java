package org.strykeforce.thirdcoast.telemetry.tct.talon.config.out;

import dagger.Subcomponent;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.SubConfigScope;

@SubConfigScope
@Subcomponent(modules = OutputMenuModule.class)
public interface OutputMenuComponent {

  @OutputMenu
  Menu menu();

  @Subcomponent.Builder
  interface Builder {

    OutputMenuComponent build();
  }

}
