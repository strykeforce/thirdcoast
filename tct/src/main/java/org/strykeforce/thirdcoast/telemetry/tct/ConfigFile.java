package org.strykeforce.thirdcoast.telemetry.tct;

import com.moandjiezana.toml.Toml;
import java.io.File;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigFile {

  final static Logger logger = LoggerFactory.getLogger(ConfigFile.class);
  private final File file;
  private Toml config = new Toml();

  @Inject
  public ConfigFile(File file) {
    this.file = file;
  }

  public Toml load() {
    config.read(file);
    return config;
  }

  public void save(Toml newConfig) {
  }

}
