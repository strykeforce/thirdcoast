package org.strykeforce.thirdcoast.talon;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;
import com.electronwill.nightconfig.toml.TomlConfig;

class VoltageTalonParameters extends TalonParameters {

  VoltageTalonParameters(TomlConfig toml) {
    super(toml);
  }

  @Override
  public void configure(CANTalon talon) {
    super.configure(talon);
  talon.changeControlMode(TalonControlMode.Voltage);
  }

  @Override
  public String toString() {
    return "VoltageTalonParameters{} " + super.toString();
  }
}
