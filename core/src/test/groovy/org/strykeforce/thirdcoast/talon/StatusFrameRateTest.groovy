package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import spock.lang.Specification

import static com.ctre.phoenix.motorcontrol.StatusFrame.*

class StatusFrameRateTest extends Specification {

    static TIMEOUT = 10

    def "builds a StatusFrameRate"() {
        given:
        def talon = Mock(TalonSRX)

        when:
        def sfr = StatusFrameRate.builder().analogTempVbat(1).feedback(2).general(3)
                .pulseWidth(5).quadEncoder(6).build()
        sfr.configure(talon)

        then:
        1 * talon.setStatusFramePeriod(Status_4_AinTempVbat, 1, TIMEOUT)
        1 * talon.setStatusFramePeriod(Status_2_Feedback0, 2, TIMEOUT)
        1 * talon.setStatusFramePeriod(Status_1_General, 3, TIMEOUT)
//        1 * talon.setStatusFramePeriod(TalonSRX.StatusFrameRate.PulseWidth, 5)
//        1 * talon.setStatusFramePeriod(TalonSRX.StatusFrameRate.QuadEncoder, 6)
    }

    def "builds a StatusFrameRate with all defaults"() {
        given:
        def talon = Mock(TalonSRX)

        when:
        def sfr = StatusFrameRate.builder().build()
        sfr.configure(talon)

        then:
        1 * talon.setStatusFramePeriod(Status_4_AinTempVbat, 100, TIMEOUT)
        1 * talon.setStatusFramePeriod(Status_2_Feedback0, 20, TIMEOUT)
        1 * talon.setStatusFramePeriod(Status_1_General, 10, TIMEOUT)
//        1 * talon.setStatusFrameRateMs(TalonSRX.StatusFrameRate.PulseWidth, 100)
//        1 * talon.setStatusFrameRateMs(TalonSRX.StatusFrameRate.QuadEncoder, 100)
    }
}
