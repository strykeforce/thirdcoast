package org.strykeforce.thirdcoast.telemetry.tct.talon.di;

import dagger.Subcomponent;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.di.ModeScoped;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonMenu;

@ModeScoped
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
