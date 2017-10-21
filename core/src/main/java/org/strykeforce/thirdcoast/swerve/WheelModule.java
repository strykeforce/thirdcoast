package org.strykeforce.thirdcoast.swerve;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import org.strykeforce.thirdcoast.talon.TalonProvisioner;

/**
 * <a href="https://google.github.io/dagger/" target="_top">Dagger</a> dependency-injection support
 * for wheel configuration.
 */
@Module
public abstract class WheelModule {

  @Provides
  @Singleton
  public static Wheel[] provideWheels(TalonProvisioner talonProvisioner) {
    return new Wheel[]{
        new Wheel(talonProvisioner, 0), // front left
        new Wheel(talonProvisioner, 1), // front right
        new Wheel(talonProvisioner, 2), // rear left
        new Wheel(talonProvisioner, 3)  // rear right
    };
  }
}
