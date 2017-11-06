package org.strykeforce.thirdcoast.telemetry.tct.dio;

import dagger.Subcomponent;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.di.ModeScoped;

@ModeScoped
@Subcomponent(modules = {
    DioMenuModule.class,
})
public interface DioMenuComponent {

  @DioMenu
  Menu menu();

  @Subcomponent.Builder
  interface Builder {

    DioMenuComponent build();
  }

}
