package org.strykeforce.thirdcoast.telemetry.grapher

import spock.lang.Specification

class MeasureTest extends Specification {

    def "find by id"() {
        expect:
        Measure.valueOf("CLOSED_LOOP_TARGET") == Measure.CLOSED_LOOP_TARGET
    }

    def "measure does not exist"() {
        when:
        Measure.valueOf("MISSING")

        then:
        thrown(IllegalArgumentException)
    }

 }
