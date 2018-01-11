package org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl;

import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.talon.ThirdCoastTalon;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractIntConfigCommand;

/** Configure Motion Magic Acceleration. */
public class MotionAccelerationCommand extends AbstractIntConfigCommand {

  public static final String NAME = "Motion Acceleration";

  @Inject
  MotionAccelerationCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void saveConfig(int value) {
    talonSet.talonConfigurationBuilder().motionMagicAcceleration(value);
  }

  @Override
  protected void config(ThirdCoastTalon talon, int value) {
    talon.configMotionAcceleration(value, TIMEOUT_MS);
  }
}
