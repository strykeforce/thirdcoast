package org.team2767.thirdcoast;

import dagger.BindsInstance;
import dagger.Component;
import java.io.File;
import javax.inject.Singleton;
import org.strykeforce.thirdcoast.swerve.GyroModule;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.swerve.WheelModule;
import org.strykeforce.thirdcoast.talon.TalonFactory;
import org.strykeforce.thirdcoast.talon.TalonProvisioner;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

/** This interface configures dependency injection for the Robot. */
@Singleton
@Component(
  modules = {
    GyroModule.class,
    WheelModule.class,
  }
)
interface RobotComponent {

  Controls controls();

  SwerveDrive swerveDrive();

  TelemetryService telemetryService();

  TalonProvisioner talonProvisioner();

  TalonFactory talonFactory();

  @Component.Builder
  interface Builder {

    @BindsInstance
    Builder config(File config);

    RobotComponent build();
  }
}
