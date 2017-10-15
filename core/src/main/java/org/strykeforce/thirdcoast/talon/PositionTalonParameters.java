package org.strykeforce.thirdcoast.talon;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;
import com.electronwill.nightconfig.core.UnmodifiableConfig;

class PositionTalonParameters extends PIDTalonParameters {

  PositionTalonParameters(UnmodifiableConfig config) {
    super(config);
  }

  @Override
  public void configure(CANTalon talon) {
    talon.changeControlMode(TalonControlMode.Position);
    super.configure(talon);
  }

  @Override
  public String toString() {
    return "PositionTalonParameters{} " + super.toString();
  }
}
