package org.strykeforce.thirdcoast.telemetry.app.sim;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import org.strykeforce.thirdcoast.telemetry.grapher.Inventory;

@Module
public abstract class SimulatedInventoryModule {

  @Provides
  @Singleton
  static Inventory inventory() {
    return SimulatedInventory.create();
  }

}
