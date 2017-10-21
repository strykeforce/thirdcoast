package org.strykeforce.thirdcoast.telemetry;

import dagger.Binds;
import dagger.Module;
import javax.inject.Singleton;

/**
 * <a href="https://google.github.io/dagger/" target="_top">Dagger</a> dependency-injection support
 * for {@link Inventory}.
 */
@Module
public abstract class InventoryModule {

  @Binds
  @Singleton
  abstract Inventory providesInventory(RobotInventory inv);
}
