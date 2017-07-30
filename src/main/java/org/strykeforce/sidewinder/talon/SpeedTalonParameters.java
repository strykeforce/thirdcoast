package org.strykeforce.sidewinder.talon;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;
import com.electronwill.nightconfig.toml.TomlConfig;

class SpeedTalonParameters extends PIDTalonParameters {

  SpeedTalonParameters(TomlConfig toml) {
    super(toml);
  }

  @Override
  public void configure(CANTalon talon) {
    super.configure(talon);
  talon.changeControlMode(TalonControlMode.Speed);
  }
}
