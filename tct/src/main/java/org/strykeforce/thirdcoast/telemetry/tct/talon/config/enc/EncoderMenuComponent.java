package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc;

import dagger.Subcomponent;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.SubConfigScope;

@SubConfigScope
@Subcomponent(modules = EncoderMenuModule.class)
public interface EncoderMenuComponent {

  @EncoderMenu
  Menu menu();

  @Subcomponent.Builder
  interface Builder {

    EncoderMenuComponent build();
  }

}
