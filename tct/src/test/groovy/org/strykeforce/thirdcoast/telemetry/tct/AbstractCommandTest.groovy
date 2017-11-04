package org.strykeforce.thirdcoast.telemetry.tct

import org.jline.reader.LineReader
import org.jline.terminal.Terminal
import spock.lang.Specification

class AbstractCommandTest extends Specification {

    LineReader reader = Stub(LineReader)
    Terminal terminal = Stub(Terminal)

    void setup() {
        reader.getTerminal() >> terminal
    }
}
