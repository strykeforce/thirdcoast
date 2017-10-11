package org.strykeforce.thirdcoast.telemetry.grapher

import spock.lang.Specification

class ItemTest extends Specification {

    def "checks findByJsonId input"() {
        when:
        Item.Measure.findByJsonId(-1)

        then:
        thrown(IllegalArgumentException)

        when:
        Item.Measure.findByJsonId(13)

        then:
        thrown(IllegalArgumentException)
    }
}
