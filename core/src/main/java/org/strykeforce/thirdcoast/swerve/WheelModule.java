package org.strykeforce.thirdcoast.swerve;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import org.strykeforce.thirdcoast.talon.TalonFactory;
import org.strykeforce.thirdcoast.util.Settings;

/**
 * <a href="https://google.github.io/dagger/" target="_top">Dagger</a> dependency-injection support
 * for wheel configuration.
 */
@Module
public abstract class WheelModule {

  @Provides
  @Singleton
  public static Wheel[] provideWheels(TalonFactory talonFactory, Settings settings) {
    return new Wheel[] {
      new Wheel(talonFactory, settings, 0), // front left
      new Wheel(talonFactory, settings, 1), // front right
      new Wheel(talonFactory, settings, 2), // rear left
      new Wheel(talonFactory, settings, 3) // rear right
    };
  }
}
