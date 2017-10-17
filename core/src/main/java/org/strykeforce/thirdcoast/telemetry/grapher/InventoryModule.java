package org.strykeforce.thirdcoast.telemetry.grapher;

import com.ctre.CANTalon;
import dagger.Module;
import dagger.Provides;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Singleton;

/**
 * <a href="https://google.github.io/dagger/">Dagger</a> dependency-injection support for {@link
 * Inventory}.
 */
@Module
public class InventoryModule {

  private long vers;

  @Provides
  @Singleton
  static Inventory inventory() {
    List<CANTalon> talons = new ArrayList<>(16);

    for (int i = 0; i < 64; i++) {
      try {
        CANTalon talon = new CANTalon(i);
        if (talon.GetFirmwareVersion() == 546) {
          talons.add(talon);
        }
      } catch (Throwable e) {
        System.out.println(e.getMessage());
      }
    }

    return RobotInventory.of(talons);
  }
}
