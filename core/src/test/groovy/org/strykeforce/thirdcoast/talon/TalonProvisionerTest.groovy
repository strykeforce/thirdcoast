package org.strykeforce.thirdcoast.talon

import com.ctre.CANTalon
import com.moandjiezana.toml.Toml
import spock.lang.Specification

import java.nio.file.Files

class TalonProvisionerTest extends Specification {

    def "creates config file copy from default"() {
        given:
        File temp = File.createTempFile("thirdcoast_", ".toml")
        temp.delete()
        temp.deleteOnExit()

        when:
        TalonProvisioner.checkFileExists(temp)

        then:
        Files.isReadable(temp.toPath())
        Files.size(temp.toPath()) == 806
    }

    def "default TALON config"() {
        given:
        File temp = File.createTempFile("thirdcoast_", ".toml")
        temp.delete()
        temp.deleteOnExit()
        def provisioner = new TalonProvisioner(temp)

        when:
        def config = provisioner.configurationFor("drive")

        then:
        config.name == "drive"
        config instanceof VoltageTalonConfiguration
        config.setpointMax == 12.0
        config.encoder.device == CANTalon.FeedbackDevice.QuadEncoder
        !config.encoder.isReversed()
        !config.encoder.unitScalingEnabled

        when:
        config = (PositionTalonConfiguration) provisioner.configurationFor("azimuth")

        then:
        config.name == "azimuth"
        config instanceof PositionTalonConfiguration
        config.setpointMax == 4095.0
        config.encoder.device == CANTalon.FeedbackDevice.CtreMagEncoder_Relative
        !config.encoder.isReversed()
        !config.encoder.unitScalingEnabled
        !config.brakeInNeutral
        !config.outputReversed
        config.pGain == 0.0
        config.iGain == 0.0
        config.dGain == 0.0
        config.fGain == 0.0
        config.iZone == 0
    }

    def "empty config file"() {
        given:
        File temp = File.createTempFile("thirdcoast_", ".toml")
        temp.deleteOnExit()


        when:
        def provisioner = new TalonProvisioner(temp)

        then:
        noExceptionThrown()
    }
}
