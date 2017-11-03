package org.strykeforce.thirdcoast.telemetry.tct.talon;

import dagger.Subcomponent;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;

@ModeScope
@Subcomponent(modules = {
    TalonMenuModule.class,
})
public interface TalonMenuComponent {

  @TalonMenu
  Menu menu();

  @Subcomponent.Builder
  interface Builder {

    TalonMenuComponent build();
  }

}
