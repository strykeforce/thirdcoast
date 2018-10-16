package org.strykeforce.thirdcoast.talon.config;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public interface Configurable {

  void configure(TalonSRX talon, int timeout);
}
