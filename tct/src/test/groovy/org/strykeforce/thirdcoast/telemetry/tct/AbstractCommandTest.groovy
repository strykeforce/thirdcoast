package org.strykeforce.thirdcoast.telemetry.tct

import org.jline.reader.LineReader
import org.jline.terminal.Terminal
import spock.lang.Specification

class AbstractCommandTest extends Specification {

    LineReader reader
    Terminal terminal
    PrintWriter writer

    void setup() {
        writer = Mock(PrintWriter)
        terminal = Mock(Terminal)
        terminal.writer() >> writer
        reader = Mock(LineReader)
        reader.getTerminal() >> terminal
    }
}
