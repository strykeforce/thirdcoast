package org.strykeforce.thirdcoast.talon

import com.electronwill.nightconfig.core.Config
import spock.lang.Specification

class TalonProvisionerTest extends Specification {

    def "default TALON config"() {
        given:
        def provisioner = new TalonProvisioner(TalonProvisioner.DEFAULT)

        when:
        def config = provisioner.configurationFor(TalonProvisioner.DEFAULT_CONFIG)

        then:
        config.name == TalonProvisioner.DEFAULT_CONFIG
        config instanceof VoltageTalonConfiguration
        config.setpointMax == 12.0
    }

    def "empty config"() {
        given:
        def toml = Config.inMemory()

        when:
        def provisioner = new TalonProvisioner(toml)

        then:
        noExceptionThrown()
    }

    def "empty TALON table"() {
        given:
        def toml = Config.inMemory()
        toml.add(TalonProvisioner.TALON_TABLE, new ArrayList<Config>())

        when:
        def provisioner = new TalonProvisioner(toml)

        then:
        noExceptionThrown()
    }
}
