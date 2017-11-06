package org.strykeforce.thirdcoast.telemetry.tct.talon.config.out.di;

import dagger.Subcomponent;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.di.SubConfigScoped;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.out.OutputMenu;

@SubConfigScoped
@Subcomponent(modules = OutputMenuModule.class)
public interface OutputMenuComponent {

  @OutputMenu
  Menu menu();

  @Subcomponent.Builder
  interface Builder {

    OutputMenuComponent build();
  }

}
