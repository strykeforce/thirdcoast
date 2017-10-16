package org.strykeforce.thirdcoast.telemetry.app;

import dagger.Component;
import javax.inject.Singleton;
import org.strykeforce.thirdcoast.telemetry.app.command.Command;
import org.strykeforce.thirdcoast.telemetry.app.command.CommandModule;
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

  Inventory inventory();

  GrapherController grapherController();

  Command mainCommand();

}
