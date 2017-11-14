package org.strykeforce.thirdcoast.telemetry.item

import org.strykeforce.thirdcoast.telemetry.grapher.Measure
import spock.lang.Specification

class ItemTest extends Specification {

    def "measures set by type"() {
        when:
        def measures = TalonItem.MEASURES

        then:
        !measures.contains(Measure.ANGLE)

        when:
        measures = ServoItem.MEASURES

        then:
        measures.size() == 2
        measures.contains(Measure.ANGLE)
        measures.contains(Measure.POSITION)

        when:
        measures = DigitalOutputItem.MEASURES
        then:
        measures.size() == 1
        measures.contains(Measure.VALUE)
    }


}
