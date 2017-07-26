package org.strykeforce.sidewinder.talon

import spock.lang.Specification

class LimitSwitchTest extends Specification {

    def "switch config missing"() {
        when:
        def ls = new LimitSwitch(Optional.empty())

        then:
        !ls.enabled
    }

    def "Disabled limit switch"() {
        when:
        def ls = new LimitSwitch(Optional.of("Disabled"))

        then:
        !ls.enabled
    }

    def "normally-open limit switch"() {
        when:
        def ls = new LimitSwitch(Optional.of("NormallyOpen"))


        then:
        ls.enabled
        ls.normallyOpen
    }

    def "normally-closed limit switch"() {
        when:
        def ls = new LimitSwitch(Optional.of("NormallyClosed"))

        then:
        ls.enabled
        !ls.normallyOpen
    }

    def "misconfigured limit switch"() {
        when:
        def ls = new LimitSwitch(Optional.of("FOO"))

        then:
        thrown(IllegalStateException)
    }

    def "equality and case insensitivity"() {
        when:
        def ls1 = new LimitSwitch(Optional.of("NormallyOpen"))
        def ls2 = new LimitSwitch(Optional.of("nOrMaLlYoPeN"))

        then:
        ls1 == ls2
    }

    def "inequality"() {
        when:
        def ls1 = new LimitSwitch(Optional.of("NormallyOpen"))
        def ls2 = new LimitSwitch(Optional.of("NormallyClosed"))
        def ls3 = new LimitSwitch(Optional.of("Disabled"))

        then:
        ls1 != ls2
        ls1 != ls3
    }
}
