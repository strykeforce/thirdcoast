package org.strykeforce.thirdcoast.telemetry.tct.servo;

import dagger.Subcomponent;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.di.ModeScoped;

@ModeScoped
@Subcomponent(modules = {
    ServoMenuModule.class,
})
public interface ServoMenuComponent {

  @ServoMenu
  Menu menu();

  @Subcomponent.Builder
  interface Builder {

    ServoMenuComponent build();
  }

}