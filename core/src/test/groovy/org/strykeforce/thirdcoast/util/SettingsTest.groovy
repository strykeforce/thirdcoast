package org.strykeforce.thirdcoast.util

import org.strykeforce.thirdcoast.talon.TalonProvisioner
import spock.lang.Specification

class SettingsTest extends Specification {

    def "reads defaults"() {
        when:
        def settings = new Settings(new File("/bogus"))
        def talons = settings.getTables(TalonProvisioner.TALON_TABLE)

        then:
        talons.size() == 2
        talons.get(0).getString("name") == "drive"
        talons.get(1).getString("name") == "azimuth"
    }

    def "overrides defaults"() {
        given:
        def tomlStr = '''
[[TALON]]
  name = "stryke"

[[TALON]]
  name = "force"
'''
        when:
        def settings = new Settings(tomlStr)
        def talons = settings.getTables(TalonProvisioner.TALON_TABLE)

        then:
        talons.size() == 2
        talons.get(0).getString("name") == "stryke"
        talons.get(1).getString("name") == "force"

    }
}
