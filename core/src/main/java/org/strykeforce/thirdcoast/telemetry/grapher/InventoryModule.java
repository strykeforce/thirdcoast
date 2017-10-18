package org.strykeforce.thirdcoast.telemetry.grapher;

import com.ctre.CANTalon;
import dagger.Module;
import dagger.Provides;
import java.util.Collection;
import javax.inject.Named;
import javax.inject.Singleton;
import org.strykeforce.thirdcoast.telemetry.TelemetryComponent;

/**
 * <a href="https://google.github.io/dagger/">Dagger</a> dependency-injection support for {@link
 * Inventory}.
 */
@Module
public class InventoryModule {

//  @Provides
//  static Collection<CANTalon> provideTalons(@Named("talons") Collection<CANTalon> talons) {
//    return talons;
//  }

  @Provides
  @Singleton
  static Inventory inventory(Collection<CANTalon> talons) {
    return RobotInventory.of(talons);
  }
}
