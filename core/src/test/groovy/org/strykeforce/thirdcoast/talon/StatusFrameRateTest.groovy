package org.strykeforce.thirdcoast.talon

import com.ctre.CANTalon
import spock.lang.Specification

class StatusFrameRateTest extends Specification {

    def "builds a StatusFrameRate"() {
        given:
        def talon = Mock(CANTalon)

        when:
        def sfr = StatusFrameRate.builder().analogTempVbat(1).feedback(2).general(3)
            .pulseWidth(5).quadEncoder(6).build()
        sfr.configure(talon)

        then:
        1 * talon.setStatusFrameRateMs(CANTalon.StatusFrameRate.AnalogTempVbat, 1);
        1 * talon.setStatusFrameRateMs(CANTalon.StatusFrameRate.Feedback, 2);
        1 * talon.setStatusFrameRateMs(CANTalon.StatusFrameRate.General, 3);
        1 * talon.setStatusFrameRateMs(CANTalon.StatusFrameRate.PulseWidth, 5);
        1 * talon.setStatusFrameRateMs(CANTalon.StatusFrameRate.QuadEncoder, 6);
    }

    def "builds a StatusFrameRate with all defaults"() {
        given:
        def talon = Mock(CANTalon)

        when:
        def sfr = StatusFrameRate.builder().build()
        sfr.configure(talon)

        then:
        1 * talon.setStatusFrameRateMs(CANTalon.StatusFrameRate.AnalogTempVbat, 100);
        1 * talon.setStatusFrameRateMs(CANTalon.StatusFrameRate.Feedback, 20);
        1 * talon.setStatusFrameRateMs(CANTalon.StatusFrameRate.General, 10);
        1 * talon.setStatusFrameRateMs(CANTalon.StatusFrameRate.PulseWidth, 100);
        1 * talon.setStatusFrameRateMs(CANTalon.StatusFrameRate.QuadEncoder, 100);
    }
}
