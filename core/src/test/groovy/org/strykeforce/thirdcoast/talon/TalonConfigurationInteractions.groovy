package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.ErrorCode
import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod
import spock.lang.Specification

import static com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput
import static com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder
import static com.ctre.phoenix.motorcontrol.LimitSwitchNormal.*
import static com.ctre.phoenix.motorcontrol.LimitSwitchSource.Deactivated
import static com.ctre.phoenix.motorcontrol.LimitSwitchSource.FeedbackConnector
import static com.ctre.phoenix.motorcontrol.VelocityMeasPeriod.Period_100Ms

class TalonConfigurationInteractions extends Specification {

    static TIMEOUT = 0

    void defaultVoltageCompensationInteractions(talon) {
        1 * talon.configVoltageCompSaturation(12.0d, TIMEOUT)
        1 * talon.enableVoltageCompensation(true)
    }

    void defaultProfileSlotInteractions(talon) {
        1 * talon.getDeviceID()
        1 * talon.selectProfileSlot(0, 0)
    }

    void selectedFeedbackSensorInteraction(ThirdCoastTalon talon, FeedbackDevice device, boolean reversed) {
        1 * talon.configSelectedFeedbackSensor(device, 0, TIMEOUT) >> ErrorCode.OK
        1 * talon.getDeviceID()
        1 * talon.getDescription()
        1 * talon.setSensorPhase(reversed)
    }

    void defaultSelectedFeedbackSensorInteractions(talon) {
        selectedFeedbackSensorInteraction(talon, QuadEncoder, false)
    }

    void limitSwitchInteractions(ThirdCoastTalon talon, Boolean fwdNormallyOpen, Boolean revNormallyOpen) {
        1 * talon.overrideLimitSwitchesEnable(fwdNormallyOpen != null || revNormallyOpen != null)
        if (fwdNormallyOpen == null) {
            1 * talon.configForwardLimitSwitchSource(Deactivated, Disabled, TIMEOUT)
        } else {
            1 * talon.configForwardLimitSwitchSource(FeedbackConnector,
                    fwdNormallyOpen ? NormallyOpen : NormallyClosed,
                    TIMEOUT)
        }

        if (revNormallyOpen == null) {
            1 * talon.configReverseLimitSwitchSource(Deactivated, Disabled, TIMEOUT)
        } else {
            1 * talon.configReverseLimitSwitchSource(FeedbackConnector,
                    revNormallyOpen ? NormallyOpen : NormallyClosed,
                    TIMEOUT)
        }
    }

    void defaultLimitSwitchInteractions(talon) {
        limitSwitchInteractions(talon, null, null)
    }

    void defaultForwardSoftLimitInteractions(talon) {
        1 * talon.configForwardSoftLimitEnable(false, TIMEOUT)
        1 * talon.configForwardSoftLimitThreshold(0, TIMEOUT)
    }

    void defaultReverseSoftLimitInteractions(talon) {
        1 * talon.configReverseSoftLimitEnable(false, TIMEOUT)
        1 * talon.configReverseSoftLimitThreshold(0, TIMEOUT)
    }

    void softLimitInteractions(ThirdCoastTalon talon, Integer fwdLimit, Integer revLimit) {
        1 * talon.overrideSoftLimitsEnable(fwdLimit != null || revLimit != null)
        1 * talon.configForwardSoftLimitEnable(fwdLimit != null, TIMEOUT)
        1 * talon.configForwardSoftLimitThreshold(
                fwdLimit != null ? fwdLimit : 0,
                TIMEOUT)
        1 * talon.configReverseSoftLimitEnable(revLimit != null, TIMEOUT)
        1 * talon.configReverseSoftLimitThreshold(
                revLimit != null ? revLimit : 0,
                TIMEOUT)
    }

    void defaultSoftLimitInteractions(talon) {
        softLimitInteractions(talon, null, null)
    }

    void currentLimitInteractions(ThirdCoastTalon talon, int cont, int peak) {
        1 * talon.configContinuousCurrentLimit(cont, TIMEOUT)
        1 * talon.configPeakCurrentLimit(peak, TIMEOUT)
        1 * talon.enableCurrentLimit(cont > 0)
    }

    void defaultCurrentLimitInteractions(talon) {
        currentLimitInteractions(talon, 0, 0)
    }

    void velocityMeasurementInteractions(ThirdCoastTalon talon, VelocityMeasPeriod period, int window) {
        1 * talon.configVelocityMeasurementPeriod(period, TIMEOUT)
        1 * talon.configVelocityMeasurementWindow(window, TIMEOUT)
    }

    void defaultVelocityMeasurementInteractions(talon) {
        velocityMeasurementInteractions(talon, Period_100Ms, 64)
    }

    void defaultControlModeInteractions(talon) {
        1 * talon.changeControlMode(PercentOutput)
    }

    void defaultNeutralModeInteractions(talon) {
        1 * talon.setNeutralMode(NeutralMode.Coast)
    }

    void defaultInvertedInteractions(talon) {
        1 * talon.setInverted(false)
    }

    void defaultOpenLoopRampInteractions(talon) {
        1 * talon.configOpenloopRamp(0d, TIMEOUT)
    }

    void defaultTalonInteraction(talon) {
        defaultNeutralModeInteractions(talon)
        defaultInvertedInteractions(talon)
        defaultOpenLoopRampInteractions(talon)
        defaultVoltageCompensationInteractions(talon)
        defaultSelectedFeedbackSensorInteractions(talon)
        defaultLimitSwitchInteractions(talon)
        defaultSoftLimitInteractions(talon)
        defaultCurrentLimitInteractions(talon)
        defaultProfileSlotInteractions(talon)
        defaultVelocityMeasurementInteractions(talon)
    }
}
