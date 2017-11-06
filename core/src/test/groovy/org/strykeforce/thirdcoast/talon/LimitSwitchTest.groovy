package org.strykeforce.thirdcoast.talon

import spock.lang.Specification

class LimitSwitchTest extends Specification {

    def "switch config missing"() {
        when:
        def ls = new LimitSwitch(null)

        then:
        !ls.enabled
    }

    def "Disabled limit switch"() {
        when:
        def ls = new LimitSwitch("Disabled")

        then:
        !ls.enabled
    }

    def "normally-open limit switch"() {
        when:
        def ls = new LimitSwitch("NormallyOpen")


        then:
        ls.enabled
        ls.normallyOpen
    }

    def "normally-closed limit switch"() {
        when:
        def ls = new LimitSwitch("NormallyClosed")

        then:
        ls.enabled
        !ls.normallyOpen
    }

    def "misconfigured limit switch"() {
        when:
        new LimitSwitch("FOO")

        then:
        thrown(IllegalStateException)
    }

 }
