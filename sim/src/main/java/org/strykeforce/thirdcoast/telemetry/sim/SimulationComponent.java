package org.strykeforce.thirdcoast.telemetry.sim;

import dagger.Component;
import javax.inject.Singleton;
import org.strykeforce.thirdcoast.telemetry.grapher.GrapherController;
import org.strykeforce.thirdcoast.telemetry.grapher.Inventory;
import org.strykeforce.thirdcoast.telemetry.grapher.NetworkModule;

@Singleton
@Component(modules = {
    SimulatedInventoryModule.class,
    NetworkModule.class,
})
interface SimulationComponent {

  Inventory inventory();

  GrapherController grapherController();
}
