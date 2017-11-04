package org.strykeforce.thirdcoast.telemetry.tct.talon.config

import com.ctre.CANTalon
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommandTest
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet

class AbstractTalonConfigCommandTest extends AbstractCommandTest {

    TalonSet talonSet
    CANTalon talon

    void setup() {
        talonSet = new TalonSet()
        talon  = Mock(CANTalon)
        talonSet.selected() << talon
    }
}
