package org.strykeforce.thirdcoast.telemetry.app;

import dagger.Component;
import javax.inject.Singleton;
import org.strykeforce.thirdcoast.telemetry.app.command.CommandModule;
import org.strykeforce.thirdcoast.telemetry.app.sim.SimulatedInventoryModule;
import org.strykeforce.thirdcoast.telemetry.grapher.NetworkModule;

@Singleton
@Component(modules = {
    SimulatedInventoryModule.class,
    NetworkModule.class,
    CommandModule.class,
})
public interface SimulationComponent extends RobotComponent { }
