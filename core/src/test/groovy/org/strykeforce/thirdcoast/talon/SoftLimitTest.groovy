package org.strykeforce.thirdcoast.talon

import spock.lang.Specification

class SoftLimitTest extends Specification {

    def "config missing"() {
        when:
        def sl = new SoftLimit(null)

        then:
        !sl.enabled
    }

    def "config present"() {
        when:
        def sl = new SoftLimit(123.0)

        then:
        sl.enabled
        sl.value == 123.0
    }
}
