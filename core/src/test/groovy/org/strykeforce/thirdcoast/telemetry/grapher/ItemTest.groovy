package org.strykeforce.thirdcoast.telemetry.grapher

import spock.lang.Specification

class ItemTest extends Specification {

    def "measures set by type"() {
        when:
        def measures = Item.Type.valueOf("TALON").measures()

        then:
        measures.size() == 13
        !measures.contains(Measure.ANGLE)
        !measures.contains(Measure.POSITION)

        when:
        measures = Item.Type.valueOf("SERVO").measures()

        then:
        measures.size() == 2
        measures.contains(Measure.ANGLE)
        measures.contains(Measure.POSITION)

        when:
        measures = Item.Type.valueOf("DIGITAL_INPUT").measures()

        then:
        measures.size() == 1
        measures.contains(Measure.VALUE)
    }


}
