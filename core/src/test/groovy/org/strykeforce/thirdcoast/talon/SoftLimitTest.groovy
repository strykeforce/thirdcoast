package org.strykeforce.thirdcoast.talon

import com.moandjiezana.toml.Toml
import com.moandjiezana.toml.TomlWriter
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
        def sl = new SoftLimit(123)

        then:
        sl.enabled
        sl.position == 123
    }

    def "creates instance from full TOML"() {
        def input = '''
enabled = true
position = 2767
'''
        given:
        def toml = new Toml().read(input)
        when:
        def softLimit = toml.to(SoftLimit.class)

        then:
        with(softLimit) {
            enabled
            position == 2767
        }
    }

    def "creates instance from partial TOML"() {
        def input = '''
enabled = false
'''
        given:
        def toml = new Toml().read(input)
        when:
        def softLimit = toml.to(SoftLimit.class)

        then:
        with(softLimit) {
            !enabled
            position == 0
        }
    }

    def "creates instance from no TOML"() {
        given:
        def toml = new Toml().read('')
        when:
        def softLimit = toml.to(SoftLimit.class)

        then:
        with(softLimit) {
            !enabled
            position == 0
        }
    }

    def "serializes into TOML"() {
        given:
        def softLimit = new SoftLimit(2767)
        def writer = new TomlWriter()

        when:
        def output = writer.write(softLimit)

        then:
        output == '''enabled = true
position = 2767
'''
    }

}
