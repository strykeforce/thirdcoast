package org.strykeforce.thirdcoast.swerve;

import com.kauailabs.navx.frc.AHRS;
import dagger.Module;
import dagger.Provides;
import edu.wpi.first.wpilibj.SPI.Port;
import javax.inject.Singleton;

/**
 * <a href="https://google.github.io/dagger/" target="_top">Dagger</a> dependency-injection support
 * for gyro configuration.
 */
@Module
public abstract class GyroModule {

  @Provides
  @Singleton
  public static AHRS provideGyro() {
    return new AHRS(Port.kMXP);
  }

}
