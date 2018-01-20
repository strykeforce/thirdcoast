package org.strykeforce.thirdcoast.telemetry.tct.talon.config


import org.strykeforce.thirdcoast.talon.TalonConfigurationBuilder
import org.strykeforce.thirdcoast.talon.ThirdCoastTalon
import org.strykeforce.thirdcoast.telemetry.TelemetryService
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommandTest
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet

import javax.inject.Provider

class AbstractTalonConfigCommandTest extends AbstractCommandTest {

    TalonSet talonSet
    ThirdCoastTalon talon
    TelemetryService telemetryService = Stub()

    void setup() {
        talonSet = new TalonSet(telemetryService)
        talon  = Mock(ThirdCoastTalon)
        talonSet.selectTalon(talon)
    }
}
