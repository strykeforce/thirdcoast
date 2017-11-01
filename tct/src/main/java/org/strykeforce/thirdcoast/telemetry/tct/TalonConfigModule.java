package org.strykeforce.thirdcoast.telemetry.tct;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import org.strykeforce.thirdcoast.talon.TalonProvisioner;

@Module
public abstract class TalonConfigModule {

  @Singleton
  @Provides
  public static UnmodifiableConfig provideTalonConfig() {
    return TalonProvisioner.DEFAULT;
  }

}
