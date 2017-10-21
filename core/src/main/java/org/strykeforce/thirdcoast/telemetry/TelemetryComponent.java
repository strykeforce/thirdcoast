package org.strykeforce.thirdcoast.telemetry;

import dagger.BindsInstance;
import dagger.Component;
import java.util.Collection;
import javax.inject.Singleton;
import org.strykeforce.thirdcoast.telemetry.item.Item;

/**
 * This interface configures dependency injection for the TelemetryService.
 *
 * Component interfaces are set up for particular uses of different modules, for example this
 * component has a sibling SimulationComponent used for simulating an Inventory during Telemetry
 * client development. The
 *
 * @see InventoryModule
 * @see NetworkModule
 */
@Singleton
@Component(modules = {
    InventoryModule.class,
    NetworkModule.class,
})
interface TelemetryComponent {

  Inventory inventory();

  TelemetryController telemetryController();

//  Collection<Item> items();

  @Component.Builder
  interface Builder {

    @BindsInstance
    Builder items(Collection<Item> items);

    TelemetryComponent build();
  }

}
