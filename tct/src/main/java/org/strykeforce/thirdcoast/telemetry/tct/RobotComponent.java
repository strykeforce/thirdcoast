package org.strykeforce.thirdcoast.telemetry.tct;

import com.ctre.CANTalon;
import dagger.BindsInstance;
import dagger.Component;
import java.util.Collection;
import javax.inject.Singleton;
import org.strykeforce.thirdcoast.telemetry.tct.command.Command;
import org.strykeforce.thirdcoast.telemetry.tct.command.CommandModule;
import org.strykeforce.thirdcoast.telemetry.grapher.GrapherController;
import org.strykeforce.thirdcoast.telemetry.grapher.Inventory;
import org.strykeforce.thirdcoast.telemetry.grapher.InventoryModule;
import org.strykeforce.thirdcoast.telemetry.grapher.NetworkModule;

@Singleton
@Component(modules = {
    InventoryModule.class,
    NetworkModule.class,
    CommandModule.class,
})
interface RobotComponent {
  // TODO: this class appears to not be used

  Inventory inventory();

  GrapherController grapherController();

  Command mainCommand();

  @Component.Builder
  interface Builder {

    @BindsInstance
    Builder talons(Collection<CANTalon> talons);

    RobotComponent build();
  }

}
