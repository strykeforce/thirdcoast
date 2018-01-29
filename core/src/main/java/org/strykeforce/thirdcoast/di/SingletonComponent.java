package org.strykeforce.thirdcoast.di;

import dagger.BindsInstance;
import dagger.Component;
import java.util.Collection;
import javax.inject.Singleton;
import org.strykeforce.thirdcoast.telemetry.InventoryModule;
import org.strykeforce.thirdcoast.telemetry.NetworkModule;
import org.strykeforce.thirdcoast.telemetry.TelemetryController;
import org.strykeforce.thirdcoast.telemetry.item.Item;

/**
 * This interface configures dependency injection for the TelemetryService.
 *
 * <p>Component interfaces are set up for particular uses of different modules, for example this
 * component has a sibling SimulationComponent used for simulating an Inventory during Telemetry
 * client development. The
 *
 * @see InventoryModule
 * @see NetworkModule
 */
@Singleton
@Component(
  modules = {
    InventoryModule.class,
    NetworkModule.class,
  }
)
public interface SingletonComponent {

  //  Inventory inventory();

  TelemetryController telemetryController();

  @Component.Builder
  interface Builder {

    @BindsInstance
    Builder items(Collection<Item> items);

    SingletonComponent build();
  }
}
