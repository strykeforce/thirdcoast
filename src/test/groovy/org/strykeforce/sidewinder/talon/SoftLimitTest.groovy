package org.strykeforce.sidewinder.talon

import spock.lang.Specification

class SoftLimitTest extends Specification {

    def "config missing"() {
        when:
        def sl = new SoftLimit(Optional.empty())

        then:
        !sl.enabled
    }

    def "config present"() {
        when:
        def sl = new SoftLimit(Optional.of(Double.valueOf(123.0)))

        then:
        sl.enabled
        sl.value == 123.0
    }
}
