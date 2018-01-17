package org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl;

import com.ctre.CANTalon;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractDoubleConfigCommand;

/** Configure Motion Magic Cruise Velocity. */
public class MotionMagicCruiseVelocityCommand extends AbstractDoubleConfigCommand {

  public static final String NAME = "Motion Magic Cruise Velocity";

  @Inject
  public MotionMagicCruiseVelocityCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void saveConfig(double value) {
    talonSet.talonConfigurationBuilder().motionMagicCruiseVelocity(value);
  }

  @Override
  protected void config(CANTalon talon, double value) {
    talon.setMotionMagicCruiseVelocity(value);
  }
}
