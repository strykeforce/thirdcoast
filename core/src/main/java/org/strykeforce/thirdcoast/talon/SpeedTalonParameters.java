package org.strykeforce.thirdcoast.talon;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;
import com.electronwill.nightconfig.core.UnmodifiableConfig;

class SpeedTalonParameters extends PIDTalonParameters {

  SpeedTalonParameters(UnmodifiableConfig config) {
    super(config);
  }

  @Override
  public void configure(CANTalon talon) {
    super.configure(talon);
    talon.changeControlMode(TalonControlMode.Speed);
  }
}
