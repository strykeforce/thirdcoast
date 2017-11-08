package org.strykeforce.thirdcoast.talon

import com.moandjiezana.toml.Toml
import com.moandjiezana.toml.TomlWriter
import spock.lang.Specification

class LimitSwitchTest extends Specification {

    def "switch config missing"() {
        when:
        def ls = new LimitSwitch()

        then:
        !ls.enabled
    }

    def "Disabled limit switch"() {
        when:
        def ls = new LimitSwitch(false,false)

        then:
        !ls.enabled
    }

    def "normally-open limit switch"() {
        when:
        def ls = new LimitSwitch(true, true)


        then:
        ls.enabled
        ls.normallyOpen
    }

    def "normally-closed limit switch"() {
        when:
        def ls = new LimitSwitch(true, false)

        then:
        ls.enabled
        !ls.normallyOpen
    }

    def "creates instance from full TOML"() {
        def input = '''
enabled = true
normallyOpen = true
'''
        given:
        def toml = new Toml().read(input)
        when:
        def limitSwitch = toml.to(LimitSwitch.class)

        then:
        with(limitSwitch) {
            enabled
            normallyOpen
        }
    }

    def "creates instance from partial TOML"() {
        def input = '''
enabled = false
'''
        given:
        def toml = new Toml().read(input)
        when:
        def limitSwitch = toml.to(LimitSwitch.class)

        then:
        with(limitSwitch) {
            !enabled
            !normallyOpen
        }
    }

    def "creates instance from no TOML"() {
        given:
        def toml = new Toml().read('')
        when:
        def limitSwitch = toml.to(LimitSwitch.class)

        then:
        with(limitSwitch) {
            !enabled
            !normallyOpen
        }
    }

    def "serializes into TOML"() {
        given:
        def limitSwitch = new LimitSwitch(true,false)
        def writer = new TomlWriter()

        when:
        def output = writer.write(limitSwitch)

        then:
        output == '''enabled = true
normallyOpen = false
'''
    }
}
