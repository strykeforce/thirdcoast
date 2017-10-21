package org.strykeforce.thirdcoast.swerve;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 * <a href="https://google.github.io/dagger/" target="_top">Dagger</a> dependency-injection support
 * for wheel configuration.
 */
@Module
public abstract class WheelModule {

  @Provides
  @Singleton
  public static Wheel[] provideWheels() {
    return new Wheel[]{
        new Wheel(0), // front left
        new Wheel(1), // front right
        new Wheel(2), // rear left
        new Wheel(3)  // rear right
    };
  }
}
