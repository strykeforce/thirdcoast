package org.strykeforce.thirdcoast.telemetry.grapher;

import com.ctre.CANTalon;
import dagger.Module;
import dagger.Provides;
import java.util.Collection;
import javax.inject.Singleton;

/**
 * <a href="https://google.github.io/dagger/" target="_top">Dagger</a> dependency-injection support
 * for {@link Inventory}.
 */
@Module
public class InventoryModule {

  @Provides
  @Singleton
  static Inventory inventory(Collection<CANTalon> talons) {
    return RobotInventory.of(talons);
  }
}
