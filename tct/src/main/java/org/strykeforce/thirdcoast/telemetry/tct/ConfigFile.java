package org.strykeforce.thirdcoast.telemetry.tct;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import org.strykeforce.thirdcoast.talon.TalonConfigurationBuilder;
import org.strykeforce.thirdcoast.talon.TalonProvisioner;

public class ConfigFile {

  private final FileConfig config;

  @Inject
  public ConfigFile(FileConfig config) {
    this.config = config;
  }

  public UnmodifiableConfig load() {
    config.load();
    return config.unmodifiable();
  }

  public void save(UnmodifiableConfig newConfig) {
    List<UnmodifiableConfig> configList = config.get(TalonProvisioner.TALON_TABLE);
    configList.add(newConfig);
    this.config.set(TalonProvisioner.TALON_TABLE, configList);
    this.config.save();
  }
}
