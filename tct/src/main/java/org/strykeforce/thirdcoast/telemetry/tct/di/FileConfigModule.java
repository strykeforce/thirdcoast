package org.strykeforce.thirdcoast.telemetry.tct.di;

import com.electronwill.nightconfig.core.file.FileConfig;
import dagger.Module;
import dagger.Provides;
import java.io.File;
import javax.inject.Singleton;

@Module
public abstract class FileConfigModule {

  @Provides
  @Singleton
  public static FileConfig provideFileConfig(File file) {
    return FileConfig.of(file);
  }

}
