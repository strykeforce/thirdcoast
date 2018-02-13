package org.team2767.thirdcoast;

import dagger.BindsInstance;
import dagger.Component;
import java.net.URL;
import javax.inject.Singleton;
import org.strykeforce.thirdcoast.swerve.GraphableSwerveDriveModule;
import org.strykeforce.thirdcoast.swerve.GyroModule;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.swerve.WheelModule;
import org.strykeforce.thirdcoast.talon.Talons;
import org.strykeforce.thirdcoast.telemetry.NetworkModule;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

/** This interface configures dependency injection for the Robot. */
@Singleton
@Component(
  modules = {
    NetworkModule.class,
    GyroModule.class,
    WheelModule.class,
    GraphableSwerveDriveModule.class,
  }
)
interface RobotComponent {

  Controls controls();

  SwerveDrive swerveDrive();

  TelemetryService telemetryService();

  Talons talons();

  @Component.Builder
  interface Builder {

    @BindsInstance
    Builder config(URL config);

    RobotComponent build();
  }
}
