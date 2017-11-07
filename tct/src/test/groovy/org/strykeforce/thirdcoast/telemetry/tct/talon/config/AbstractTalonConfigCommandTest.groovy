package org.strykeforce.thirdcoast.telemetry.tct.talon.config

import com.ctre.CANTalon
import org.strykeforce.thirdcoast.talon.TalonConfigurationBuilder
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommandTest
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet

import javax.inject.Provider

class AbstractTalonConfigCommandTest extends AbstractCommandTest {

    TalonSet talonSet
    CANTalon talon
    Provider<TalonConfigurationBuilder> builder = Stub()

    void setup() {
        builder.get() >> new TalonConfigurationBuilder()
        talonSet = new TalonSet(builder)
        talon  = Mock(CANTalon)
        talonSet.selected() << talon
    }
}
