package org.strykeforce.thirdcoast.swerve;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module
public class GraphableSwerveDriveModule {

  @Provides
  @Singleton
  public static SwerveDrive swerveDrive(GraphableSwerveDrive swerveDrive) {
    return swerveDrive;
  }
}
