package org.strykeforce.thirdcoast.telemetry.item

import com.ctre.phoenix.ParamEnum
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.Faults
import com.ctre.phoenix.motorcontrol.SensorCollection
import com.ctre.phoenix.motorcontrol.StickyFaults
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.squareup.moshi.JsonWriter
import org.strykeforce.thirdcoast.telemetry.grapher.Measure
import org.strykeforce.thirdcoast.telemetry.grapher.Measure.*
import java.io.IOException
import java.util.*
import java.util.function.DoubleSupplier

private const val TYPE = "talon"

/** Represents a [TalonSRX] telemetry-enable Item.  */
class TalonItem @JvmOverloads constructor(val talon: TalonSRX, description: String? = defaultDescription(talon)) :
  AbstractItem(TYPE, description ?: defaultDescription(talon), MEASURES) {
  private val sensorCollection: SensorCollection = talon.sensorCollection

  override fun deviceId(): Int {
    return talon.deviceID
  }

  override fun measurementFor(measure: Measure): DoubleSupplier {
    if (!MEASURES.contains(measure)) {
      throw IllegalArgumentException("invalid measure: " + measure.name)
    }

    return when (measure) {
      CLOSED_LOOP_TARGET -> DoubleSupplier {
        talon.getClosedLoopTarget(0).toDouble()
      }
      OUTPUT_CURRENT -> DoubleSupplier {
        talon.outputCurrent
      }
      OUTPUT_VOLTAGE -> DoubleSupplier {
        talon.motorOutputVoltage
      }
      OUTPUT_PERCENT -> DoubleSupplier {
        talon.motorOutputPercent
      }
      SELECTED_SENSOR_POSITION -> DoubleSupplier {
        talon.getSelectedSensorPosition(0).toDouble()
      }
      SELECTED_SENSOR_VELOCITY -> DoubleSupplier {
        talon.getSelectedSensorVelocity(0).toDouble()
      }
      ACTIVE_TRAJECTORY_POSITION -> DoubleSupplier {
        talon.activeTrajectoryPosition.toDouble()
      }
      ACTIVE_TRAJECTORY_VELOCITY -> DoubleSupplier {
        talon.activeTrajectoryVelocity.toDouble()
      }
      CLOSED_LOOP_ERROR -> DoubleSupplier {
        talon.getClosedLoopError(0).toDouble()
      }
      BUS_VOLTAGE -> DoubleSupplier {
        talon.busVoltage
      }
      ERROR_DERIVATIVE -> DoubleSupplier {
        talon.getErrorDerivative(0)
      }
      INTEGRAL_ACCUMULATOR -> DoubleSupplier {
        talon.getIntegralAccumulator(0)
      }
      ANALOG_IN -> DoubleSupplier {
        sensorCollection.analogIn.toDouble()
      }
      ANALOG_RAW -> DoubleSupplier {
        sensorCollection.analogInRaw.toDouble()
      }
      ANALOG_VELOCITY -> DoubleSupplier {
        sensorCollection.analogInVel.toDouble()
      }
      QUAD_POSITION -> DoubleSupplier {
        sensorCollection.quadraturePosition.toDouble()
      }
      QUAD_VELOCITY -> DoubleSupplier {
        sensorCollection.quadratureVelocity.toDouble()
      }
      QUAD_A_PIN -> DoubleSupplier {
        if (sensorCollection.pinStateQuadA) TRUE else FALSE
      }
      QUAD_B_PIN -> DoubleSupplier {
        if (sensorCollection.pinStateQuadB) TRUE else FALSE
      }
      QUAD_IDX_PIN -> DoubleSupplier {
        if (sensorCollection.pinStateQuadIdx) TRUE else FALSE
      }
      PULSE_WIDTH_POSITION -> DoubleSupplier {
        (sensorCollection.pulseWidthPosition and 0xFFF).toDouble()
      }
      PULSE_WIDTH_VELOCITY -> DoubleSupplier {
        sensorCollection.pulseWidthVelocity.toDouble()
      }
      PULSE_WIDTH_RISE_TO_FALL -> DoubleSupplier {
        sensorCollection.pulseWidthRiseToFallUs.toDouble()
      }
      PULSE_WIDTH_RISE_TO_RISE -> DoubleSupplier {
        sensorCollection.pulseWidthRiseToRiseUs.toDouble()
      }
      FORWARD_LIMIT_SWITCH_CLOSED -> DoubleSupplier {
        if (sensorCollection.isFwdLimitSwitchClosed) TRUE else FALSE
      }
      REVERSE_LIMIT_SWITCH_CLOSED -> DoubleSupplier {
        if (sensorCollection.isRevLimitSwitchClosed) TRUE else FALSE
      }
      else -> throw AssertionError(measure)
    }
  }

  override fun toString(): String {
    return "TalonItem{" + "talon=" + talon + "} " + super.toString()
  }

  @Throws(IOException::class)
  override fun toJson(writer: JsonWriter) { // FIXME: finish 2018 conversion
    writer.beginObject()
    writer.name("type").value(TYPE)
    writer.name("baseId").value(talon.baseID.toLong())
    writer.name("deviceId").value(talon.deviceID.toLong())
    writer.name("description").value("Talon " + talon.deviceID)
    writer.name("firmwareVersion").value(talon.firmwareVersion.toLong())
    writer.name("controlMode").value(talon.controlMode.toString())
    // writer.name("brakeEnabledDuringNeutral").value(talon.getBrakeEnableDuringNeutral());
    writer
      .name("onBootBrakeMode")
      .value(talon.configGetParameter(ParamEnum.eOnBoot_BrakeMode, 0, 0))
    writer.name("busVoltage").value(talon.busVoltage)
    writer
      .name("feedbackSensorType")
      .value(talon.configGetParameter(ParamEnum.eFeedbackSensorType, 0, 0))
    writer
      .name("peakCurrentLimitMs")
      .value(talon.configGetParameter(ParamEnum.ePeakCurrentLimitMs, 0, 0))
    writer
      .name("peakCurrentLimitAmps")
      .value(talon.configGetParameter(ParamEnum.ePeakCurrentLimitAmps, 0, 0))

    // writer.name("encoderCodesPerRef").value(NA);
    writer.name("inverted").value(talon.inverted)
    // writer.name("numberOfQuadIdxRises").value(talon.getNumberOfQuadIdxRises());
    writer
      .name("eQuadIdxPolarity")
      .value(talon.configGetParameter(ParamEnum.eQuadIdxPolarity, 0, 0))
    writer.name("outputVoltage").value(talon.motorOutputVoltage)
    writer.name("outputCurrent").value(talon.outputCurrent)

    writer.name("analogInput")
    writer.beginObject()
    writer.name("position").value(talon.sensorCollection.analogIn.toLong())
    writer.name("velocity").value(talon.sensorCollection.analogInVel.toLong())
    writer.name("raw").value(talon.sensorCollection.analogInRaw.toLong())
    writer.endObject()

    writer.name("encoder")
    writer.beginObject()
    writer.name("position").value(talon.getSelectedSensorPosition(0).toLong())
    writer.name("velocity").value(talon.getSelectedSensorVelocity(0).toLong())
    writer.endObject()

    writer.name("quadrature")
    writer.beginObject()
    writer.name("position").value(talon.sensorCollection.quadraturePosition.toLong())
    writer.name("velocity").value(talon.sensorCollection.quadratureVelocity.toLong())
    writer.endObject()

    writer.name("pulseWidth")
    writer.beginObject()
    writer.name("position").value(talon.sensorCollection.pulseWidthPosition.toLong())
    writer.name("velocity").value(talon.sensorCollection.pulseWidthVelocity.toLong())
    writer.name("riseToFallUs").value(talon.sensorCollection.pulseWidthRiseToFallUs.toLong())
    writer.name("riseToRiseUs").value(talon.sensorCollection.pulseWidthRiseToRiseUs.toLong())
    writer.endObject()

    writer.name("closedLoop")
    writer.beginObject()

    writer.name("enabled").value(true)
    writer.name("p").value(talon.configGetParameter(ParamEnum.eProfileParamSlot_P, 0, 0))
    writer.name("i").value(talon.configGetParameter(ParamEnum.eProfileParamSlot_I, 0, 0))
    writer.name("d").value(talon.configGetParameter(ParamEnum.eProfileParamSlot_D, 0, 0))
    writer.name("f").value(talon.configGetParameter(ParamEnum.eProfileParamSlot_F, 0, 0))
    writer
      .name("iAccum")
      .value(talon.configGetParameter(ParamEnum.eProfileParamSlot_MaxIAccum, 0, 0))
    writer.name("iZone").value(talon.configGetParameter(ParamEnum.eProfileParamSlot_IZone, 0, 0))
    writer.name("errorInt").value(talon.getClosedLoopError(0).toLong())
    writer.name("errorDouble").value(talon.getErrorDerivative(0))
    writer.name("rampRate").value(talon.configGetParameter(ParamEnum.eOpenloopRamp, 0, 0))
    writer.name("nominalVoltage").value(talon.configGetParameter(ParamEnum.eClosedloopRamp, 0, 0))
    writer.endObject()

    writer.name("motionMagic")
    writer.beginObject()
    if (talon.controlMode == ControlMode.MotionMagic) {
      writer.name("enabled").value(true)
      writer.name("acceleration").value(talon.configGetParameter(ParamEnum.eMotMag_Accel, 0, 0))
      // writer.name("actTrajPosition").value(talon.getMotionMagicActTrajPosition());
      // writer.name("actTrajVelocity").value(talon.getMotionMagicActTrajVelocity());
      writer
        .name("cruiseVelocity")
        .value(talon.configGetParameter(ParamEnum.eMotMag_VelCruise, 0, 0))
    } else {
      writer.name("enabled").value(false)
    }
    writer.endObject()

    writer.name("motionProfile")
    writer.beginObject()
    if (talon.controlMode == ControlMode.MotionProfile) {
      writer.name("enabled").value(true)
      writer.name("topLevelBufferCount").value(talon.motionProfileTopLevelBufferCount.toLong())
    } else {
      writer.name("enabled").value(false)
    }
    writer.endObject()

    writer.name("forwardSoftLimit")
    writer.beginObject()
    writer.name("enabled").value(talon.configGetParameter(ParamEnum.eForwardSoftLimitEnable, 0, 0))
    writer
      .name("limit")
      .value(talon.configGetParameter(ParamEnum.eForwardSoftLimitThreshold, 0, 0))
    writer.endObject()

    writer.name("reverseSoftLimit")
    writer.beginObject()
    writer.name("enabled").value(talon.configGetParameter(ParamEnum.eReverseSoftLimitEnable, 0, 0))
    writer
      .name("limit")
      .value(talon.configGetParameter(ParamEnum.eReverseSoftLimitThreshold, 0, 0))
    writer.endObject()

    writer.name("lastError").value(talon.lastError.toString())
    writer.name("faults")
    writer.beginObject()
    val stickyfaults = StickyFaults()
    val faults = Faults()
    talon.getStickyFaults(stickyfaults)
    talon.getFaults(faults)
    writer.name("lim").value(faults.ForwardLimitSwitch)
    writer.name("stickyLim").value(stickyfaults.ForwardLimitSwitch)
    writer.name("softLim").value(faults.ForwardSoftLimit)
    writer.name("stickySoftLim").value(stickyfaults.ForwardSoftLimit)
    writer.name("hardwareFailure").value(faults.HardwareFailure)
    writer.name("overTemp").value(faults.SensorOverflow)
    writer.name("stickyOverTemp").value(stickyfaults.SensorOverflow)
    writer.name("revLim").value(faults.ReverseLimitSwitch)
    writer.name("stickyRevLim").value(stickyfaults.ReverseLimitSwitch)
    writer.name("revSoftLim").value(faults.ReverseSoftLimit)
    writer.name("stickyRevSoftLim").value(stickyfaults.ReverseSoftLimit)
    writer.name("underVoltage").value(faults.UnderVoltage)
    writer.name("stickyUnderVoltage").value(stickyfaults.UnderVoltage)
    writer.endObject()

    writer.endObject()
  }

  /**
   * Indicates if some other `TalonItem` has the same underlying Talon as this one.
   *
   * @param other the reference object with which to compare.
   * @return true if this TalonSRX has the same device ID, false otherwise.
   */
  override fun equals(other: Any?): Boolean {
    if (other === this) {
      return true
    }
    if (other !is TalonItem) {
      return false
    }
    val item = other as TalonItem?
    return item!!.talon.deviceID == talon.deviceID
  }

  /**
   * Returns a hashcode value for this TalonItem.
   *
   * @return a hashcode value for this TalonItem.
   */
  override fun hashCode(): Int {
    return talon.deviceID
  }

  companion object {
    val MEASURES: Set<Measure> = Collections.unmodifiableSet(
      EnumSet.of(
        CLOSED_LOOP_TARGET,
        OUTPUT_CURRENT,
        OUTPUT_VOLTAGE,
        OUTPUT_PERCENT,
        SELECTED_SENSOR_POSITION,
        SELECTED_SENSOR_VELOCITY,
        ACTIVE_TRAJECTORY_POSITION,
        ACTIVE_TRAJECTORY_VELOCITY,
        CLOSED_LOOP_ERROR,
        BUS_VOLTAGE,
        ERROR_DERIVATIVE,
        INTEGRAL_ACCUMULATOR,
        ANALOG_IN,
        ANALOG_RAW,
        ANALOG_POSITION,
        ANALOG_VELOCITY,
        QUAD_POSITION,
        QUAD_VELOCITY,
        QUAD_A_PIN,
        QUAD_B_PIN,
        QUAD_IDX_PIN,
        PULSE_WIDTH_POSITION,
        PULSE_WIDTH_VELOCITY,
        PULSE_WIDTH_RISE_TO_FALL,
        PULSE_WIDTH_RISE_TO_RISE,
        FORWARD_LIMIT_SWITCH_CLOSED,
        REVERSE_LIMIT_SWITCH_CLOSED
      )
    )
    // TODO: getMotionProfileStatus
    private const val TRUE = 1.0
    private const val FALSE = 0.0

    private fun defaultDescription(talon: TalonSRX?): String {
      return if (talon != null) "TalonSRX " + talon.deviceID else "NO TALON"
    }
  }
}
