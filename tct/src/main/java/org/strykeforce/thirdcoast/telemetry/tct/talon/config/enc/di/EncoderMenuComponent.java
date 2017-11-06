package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.di;

import dagger.Subcomponent;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.di.SubConfigScoped;

@SubConfigScoped
@Subcomponent(modules = EncoderMenuModule.class)
public interface EncoderMenuComponent {

  @EncoderMenu
  Menu menu();

  @Subcomponent.Builder
  interface Builder {

    EncoderMenuComponent build();
  }

}
