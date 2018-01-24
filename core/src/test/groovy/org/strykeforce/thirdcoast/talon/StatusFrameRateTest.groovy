package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import spock.lang.Specification

import static com.ctre.phoenix.motorcontrol.StatusFrameEnhanced.*

class StatusFrameRateTest extends Specification {

    static TIMEOUT = 10

    def "builds a StatusFrameRate"() {
        given:
        def talon = Mock(TalonSRX)

        when:
        def sfr = StatusFrameRate.builder().analogTempVbat(1).feedback(2).general(3)
                .pulseWidth(5).quadEncoder(6).motion(7).pidf0(8).build()
        sfr.configure(talon)

        then:
        1 * talon.setStatusFramePeriod(Status_1_General, 3, TIMEOUT)
        1 * talon.setStatusFramePeriod(Status_2_Feedback0, 2, TIMEOUT)
        1 * talon.setStatusFramePeriod(Status_3_Quadrature, 6, TIMEOUT)
        1 * talon.setStatusFramePeriod(Status_4_AinTempVbat, 1, TIMEOUT)
        1 * talon.setStatusFramePeriod(Status_8_PulseWidth, 5, TIMEOUT)
        1 * talon.setStatusFramePeriod(Status_10_MotionMagic, 7, TIMEOUT)
        1 * talon.setStatusFramePeriod(Status_13_Base_PIDF0, 8, TIMEOUT)
    }

    def "builds a StatusFrameRate with all defaults"() {
        given:
        def talon = Mock(TalonSRX)

        when:
        def sfr = StatusFrameRate.builder().build()
        sfr.configure(talon)

        then:
        1 * talon.setStatusFramePeriod(Status_1_General, 10, TIMEOUT)
        1 * talon.setStatusFramePeriod(Status_2_Feedback0, 20, TIMEOUT)
        1 * talon.setStatusFramePeriod(Status_3_Quadrature, 160, TIMEOUT)
        1 * talon.setStatusFramePeriod(Status_4_AinTempVbat, 160, TIMEOUT)
        1 * talon.setStatusFramePeriod(Status_8_PulseWidth, 160, TIMEOUT)
        1 * talon.setStatusFramePeriod(Status_10_MotionMagic, 160, TIMEOUT)
        1 * talon.setStatusFramePeriod(Status_13_Base_PIDF0, 160, TIMEOUT)
    }
}
