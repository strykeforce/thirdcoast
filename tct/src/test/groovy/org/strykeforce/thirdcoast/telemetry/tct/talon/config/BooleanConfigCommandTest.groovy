package org.strykeforce.thirdcoast.telemetry.tct.talon.config

import com.ctre.CANTalon
import org.jline.reader.LineReader
import org.jline.terminal.Terminal
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet

abstract class BooleanConfigCommandTest extends spock.lang.Specification {

    LineReader reader = Stub(LineReader)
    Terminal terminal = Stub(Terminal)
    TalonSet talonSet = new TalonSet()
    CANTalon talon = Mock(CANTalon)

    void setup() {
        reader.getTerminal() >> terminal
        talonSet.selected() << talon
    }

    def "talonSet contains Mock CANTalon"() {
        expect:
        talonSet.selected().contains(talon)
        talonSet.selected().first() instanceof CANTalon
    }
}
