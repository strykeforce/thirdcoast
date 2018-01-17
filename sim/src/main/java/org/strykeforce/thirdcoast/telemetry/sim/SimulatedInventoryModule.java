package org.strykeforce.thirdcoast.telemetry.sim;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import org.strykeforce.thirdcoast.telemetry.Inventory;

@Module
public abstract class SimulatedInventoryModule {

  @Provides
  @Singleton
  static Inventory inventory() {
    return SimulatedInventory.create();
  }
}
