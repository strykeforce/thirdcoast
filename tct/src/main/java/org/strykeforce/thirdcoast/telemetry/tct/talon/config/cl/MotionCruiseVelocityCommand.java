package org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl;

import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.talon.ThirdCoastTalon;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractIntConfigCommand;

/** Configure Motion Magic Cruise Velocity. */
public class MotionCruiseVelocityCommand extends AbstractIntConfigCommand {

  public static final String NAME = "Motion Cruise Velocity";

  @Inject
  MotionCruiseVelocityCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void saveConfig(int value) {
    talonSet.talonConfigurationBuilder().motionMagicCruiseVelocity(value);
  }

  @Override
  protected void config(ThirdCoastTalon talon, int value) {
    talon.configMotionCruiseVelocity(value, TIMEOUT_MS);
  }
}
