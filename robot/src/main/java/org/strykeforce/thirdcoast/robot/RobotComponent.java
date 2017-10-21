package org.strykeforce.thirdcoast.robot;

import dagger.Component;
import javax.inject.Singleton;
import org.strykeforce.thirdcoast.swerve.GyroModule;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.swerve.WheelModule;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

/**
 * This interface configures dependency injection for the Robot.
 *
 */
@Singleton
@Component(modules = {
    GyroModule.class,
    WheelModule.class,
})
public interface RobotComponent {

  Controls controls();

  SwerveDrive swerveDrive();

  TelemetryService telemetryService();

}
