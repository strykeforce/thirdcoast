package org.strykeforce.thirdcoast.telemetry.grapher

import spock.lang.Specification

class MeasureTest extends Specification {

    def "find by id"() {
        expect:
        Measure.valueOf("SETPOINT") == Measure.SETPOINT
    }

    def "measure does not exist"() {
        when:
        Measure.valueOf("MISSING")

        then:
        thrown(IllegalArgumentException)
    }

 }
