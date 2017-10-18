package org.strykeforce.thirdcoast.telemetry;

import com.ctre.CANTalon;
import dagger.BindsInstance;
import dagger.Component;
import java.util.Collection;
import javax.inject.Singleton;
import org.strykeforce.thirdcoast.telemetry.grapher.GrapherController;
import org.strykeforce.thirdcoast.telemetry.grapher.Inventory;
import org.strykeforce.thirdcoast.telemetry.grapher.InventoryModule;
import org.strykeforce.thirdcoast.telemetry.grapher.NetworkModule;

@Singleton
@Component(modules = {
    InventoryModule.class,
    NetworkModule.class,
})
public interface TelemetryComponent {

  Inventory inventory();

  GrapherController grapherController();

  Collection<CANTalon> talons();

  @Component.Builder
  interface Builder {

    @BindsInstance
    Builder talons(Collection<CANTalon> talons);

    TelemetryComponent build();
  }

}
