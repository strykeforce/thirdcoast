package org.strykeforce.thirdcoast.telemetry;

import dagger.BindsInstance;
import dagger.Component;
import java.util.Collection;
import javax.inject.Singleton;
import org.strykeforce.thirdcoast.telemetry.grapher.GrapherController;
import org.strykeforce.thirdcoast.telemetry.grapher.Inventory;
import org.strykeforce.thirdcoast.telemetry.grapher.InventoryModule;
import org.strykeforce.thirdcoast.telemetry.grapher.Item;
import org.strykeforce.thirdcoast.telemetry.grapher.NetworkModule;

@Singleton
@Component(modules = {
    InventoryModule.class,
    NetworkModule.class,
})
interface TelemetryComponent {

  Inventory inventory();

  GrapherController grapherController();

  Collection<Item> items();

  @Component.Builder
  interface Builder {

    @BindsInstance
    Builder items(Collection<Item> items);

    TelemetryComponent build();
  }

}
