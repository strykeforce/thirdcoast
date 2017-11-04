package org.strykeforce.thirdcoast.telemetry.tct.talon.config

import com.ctre.CANTalon
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommandTest
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet

class TalonConfigCommandTest extends AbstractCommandTest {

    TalonSet talonSet = new TalonSet()
    CANTalon talon = Mock(CANTalon)

    void setup() {
        talonSet.selected() << talon
    }
}
