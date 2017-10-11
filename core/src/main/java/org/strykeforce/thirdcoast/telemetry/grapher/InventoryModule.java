package org.strykeforce.thirdcoast.telemetry.grapher;

import dagger.Module;
import dagger.Provides;
import java.util.Collections;
import javax.inject.Singleton;

@Module
public class InventoryModule {

  @Provides
  @Singleton
  static Inventory inventory() {
    return RobotInventory.of(Collections.emptyList());
  }
}
