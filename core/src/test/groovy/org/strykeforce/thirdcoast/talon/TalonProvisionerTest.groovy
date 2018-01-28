package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.FeedbackDevice
import org.strykeforce.thirdcoast.util.Settings
import spock.lang.Specification

class TalonProvisionerTest extends Specification {

    def "default TALON config"() {
        given:
        def provisioner = new TalonProvisioner(new Settings())

        when:
        def config = provisioner.configurationFor("drive")

        then:
        config.name == "drive"
        config instanceof VoltageTalonConfiguration
        config.setpointMax == 12.0d
        config.encoder.device == FeedbackDevice.CTRE_MagEncoder_Relative
        !config.encoder.isReversed()

        when:
        config = (PositionTalonConfiguration) provisioner.configurationFor("azimuth")

        then:
        config.name == "azimuth"
        config instanceof PositionTalonConfiguration
        config.setpointMax == 0xFFF
        config.encoder.device == FeedbackDevice.CTRE_MagEncoder_Relative
        !config.encoder.isReversed()
        !config.brakeInNeutral
        !config.outputReversed
        config.PGain == 0.0
        config.IGain == 0.0
        config.DGain == 0.0
        config.FGain == 0.0
        config.IZone == 0
    }

    def "configuration timeout is updated"() {
        given:
        def provisioner = new TalonProvisioner(new Settings())

        when:
        provisioner.enableTimeout(true)
        def config = provisioner.configurationFor("drive")

        then:
        config.getTimeout() == TalonProvisioner.TIMEOUT_MS

        when:
        provisioner.enableTimeout(false)

        then:
        config.getTimeout() == 0

    }
}
