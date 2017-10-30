package org.strykeforce.thirdcoast.robot;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import dagger.BindsInstance;
import dagger.Component;
import javax.inject.Singleton;
import org.strykeforce.thirdcoast.swerve.GyroModule;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.swerve.WheelModule;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

/**
 * This interface configures dependency injection for the Robot.
 */
@Singleton
@Component(modules = {
    GyroModule.class,
    WheelModule.class,
})
interface RobotComponent {

  Controls controls();

  SwerveDrive swerveDrive();

  TelemetryService telemetryService();

  @Component.Builder
  interface Builder {

    @BindsInstance
    Builder toml(UnmodifiableConfig config);

    RobotComponent build();
  }

}
