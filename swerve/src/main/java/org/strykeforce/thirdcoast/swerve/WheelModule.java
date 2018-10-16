package org.strykeforce.thirdcoast.swerve;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import org.strykeforce.thirdcoast.talon.Talons;
import org.strykeforce.thirdcoast.util.Settings;

/**
 * <a href="https://google.github.io/dagger/" target="_top">Dagger</a> dependency-injection support
 * for wheel configuration.
 */
@Module
public abstract class WheelModule {

  @Provides
  @Singleton
  public static Wheel[] provideWheels(Talons talons, Settings settings) {
    return new Wheel[] {
      new DefaultWheel(talons, settings, 0), // front left
      new DefaultWheel(talons, settings, 1), // front right
      new DefaultWheel(talons, settings, 2), // rear left
      new DefaultWheel(talons, settings, 3) // rear right
    };
  }
}
