package org.strykeforce.thirdcoast.telemetry.grapher

import spock.lang.Specification

class MeasureTest extends Specification {

    def "find by id"() {
        expect:
        Measure.valueOf("SETPOINT") == Measure.SETPOINT
    }

 }
