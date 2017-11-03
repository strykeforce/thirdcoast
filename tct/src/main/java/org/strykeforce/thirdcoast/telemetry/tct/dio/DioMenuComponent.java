package org.strykeforce.thirdcoast.telemetry.tct.dio;

import dagger.Subcomponent;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.ModeScope;

@ModeScope
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
