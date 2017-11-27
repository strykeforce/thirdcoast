package org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl;

import com.ctre.CANTalon;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractDoubleConfigCommand;

/**
 * Configure Motion Magic Acceleration.
 */
public class MotionMagicAccelerationCommand extends AbstractDoubleConfigCommand {

  public final static String NAME = "Motion Magic Acceleration";

  @Inject
  public MotionMagicAccelerationCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void saveConfig(double value) {
    talonSet.talonConfigurationBuilder().motionMagicAcceleration(value);
  }

  @Override
  protected void config(CANTalon talon, double value) {
    talon.setMotionMagicAcceleration(value);
  }
}
