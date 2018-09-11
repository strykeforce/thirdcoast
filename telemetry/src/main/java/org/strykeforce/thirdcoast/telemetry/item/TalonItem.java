package org.strykeforce.thirdcoast.telemetry.item;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.StickyFaults;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.squareup.moshi.JsonWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.DoubleSupplier;
import org.strykeforce.thirdcoast.telemetry.grapher.Measure;

/** Represents a {@link TalonSRX} telemetry-enable Item. */
public class TalonItem extends AbstractItem {

  public static final String TYPE = "talon";
  public static final Set<Measure> MEASURES =
      Collections.unmodifiableSet(
          EnumSet.of(
              Measure.CLOSED_LOOP_TARGET,
              Measure.OUTPUT_CURRENT,
              Measure.OUTPUT_VOLTAGE,
              Measure.OUTPUT_PERCENT,
              Measure.SELECTED_SENSOR_POSITION,
              Measure.SELECTED_SENSOR_VELOCITY,
              Measure.ACTIVE_TRAJECTORY_POSITION,
              Measure.ACTIVE_TRAJECTORY_VELOCITY,
              Measure.CLOSED_LOOP_ERROR,
              Measure.BUS_VOLTAGE,
              Measure.ERROR_DERIVATIVE,
              Measure.INTEGRAL_ACCUMULATOR,
              Measure.ANALOG_IN,
              Measure.ANALOG_RAW,
              Measure.ANALOG_POSITION,
              Measure.ANALOG_VELOCITY,
              Measure.QUAD_POSITION,
              Measure.QUAD_VELOCITY,
              Measure.QUAD_A_PIN,
              Measure.QUAD_B_PIN,
              Measure.QUAD_IDX_PIN,
              Measure.PULSE_WIDTH_POSITION,
              Measure.PULSE_WIDTH_VELOCITY,
              Measure.PULSE_WIDTH_RISE_TO_FALL,
              Measure.PULSE_WIDTH_RISE_TO_RISE,
              Measure.FORWARD_LIMIT_SWITCH_CLOSED,
              Measure.REVERSE_LIMIT_SWITCH_CLOSED));
  // TODO: getMotionProfileStatus
  private static final String NA = "not available in API";
  private static final double TRUE = 1;
  private static final double FALSE = 0;
  private final TalonSRX talon;
  private final SensorCollection sensorCollection;

  public TalonItem(TalonSRX talon, String description) {
    super(TYPE, description != null ? description : defaultDescription(talon), MEASURES);
    assert (talon != null);
    this.talon = talon;
    sensorCollection = talon.getSensorCollection();
  }

  public TalonItem(TalonSRX talon) {
    this(talon, defaultDescription(talon));
  }

  private static String defaultDescription(TalonSRX talon) {
    return talon != null ? "TalonSRX " + talon.getDeviceID() : "NO TALON";
  }

  public TalonSRX getTalon() {
    return talon;
  }

  @Override
  public int deviceId() {
    return talon.getDeviceID();
  }

  @Override
  public DoubleSupplier measurementFor(final Measure measure) {
    if (!MEASURES.contains(measure)) {
      throw new IllegalArgumentException("invalid measure: " + measure.name());
    }

    switch (measure) {
      case CLOSED_LOOP_TARGET:
        return () -> talon.getClosedLoopTarget(0);
      case OUTPUT_CURRENT:
        return talon::getOutputCurrent;
      case OUTPUT_VOLTAGE:
        return talon::getMotorOutputVoltage;
      case OUTPUT_PERCENT:
        return talon::getMotorOutputPercent;
      case SELECTED_SENSOR_POSITION:
        return () -> talon.getSelectedSensorPosition(0);
      case SELECTED_SENSOR_VELOCITY:
        return () -> talon.getSelectedSensorVelocity(0);
      case ACTIVE_TRAJECTORY_POSITION:
        return talon::getActiveTrajectoryPosition;
      case ACTIVE_TRAJECTORY_VELOCITY:
        return talon::getActiveTrajectoryVelocity;
      case CLOSED_LOOP_ERROR:
        return () -> talon.getClosedLoopError(0);
      case BUS_VOLTAGE:
        return talon::getBusVoltage;
      case ERROR_DERIVATIVE:
        return () -> talon.getErrorDerivative(0);
      case INTEGRAL_ACCUMULATOR:
        return () -> talon.getIntegralAccumulator(0);
      case ANALOG_IN:
        return sensorCollection::getAnalogIn;
      case ANALOG_RAW:
        return sensorCollection::getAnalogInRaw;
        //      case ANALOG_POSITION:
        //        return () -> 0;
      case ANALOG_VELOCITY:
        return sensorCollection::getAnalogInVel;
      case QUAD_POSITION:
        return sensorCollection::getQuadraturePosition;
      case QUAD_VELOCITY:
        return sensorCollection::getQuadratureVelocity;
      case QUAD_A_PIN:
        return () -> sensorCollection.getPinStateQuadA() ? TRUE : FALSE;
      case QUAD_B_PIN:
        return () -> sensorCollection.getPinStateQuadB() ? TRUE : FALSE;
      case QUAD_IDX_PIN:
        return () -> sensorCollection.getPinStateQuadIdx() ? TRUE : FALSE;
      case PULSE_WIDTH_POSITION:
        return () -> sensorCollection.getPulseWidthPosition() & 0xFFF;
      case PULSE_WIDTH_VELOCITY:
        return sensorCollection::getPulseWidthVelocity;
      case PULSE_WIDTH_RISE_TO_FALL:
        return sensorCollection::getPulseWidthRiseToFallUs;
      case PULSE_WIDTH_RISE_TO_RISE:
        return sensorCollection::getPulseWidthRiseToRiseUs;
      case FORWARD_LIMIT_SWITCH_CLOSED:
        return () -> sensorCollection.isFwdLimitSwitchClosed() ? TRUE : FALSE;
      case REVERSE_LIMIT_SWITCH_CLOSED:
        return () -> sensorCollection.isRevLimitSwitchClosed() ? TRUE : FALSE;
      default:
        throw new AssertionError(measure);
    }
  }

  @Override
  public String toString() {
    return "TalonItem{" + "talon=" + talon + "} " + super.toString();
  }

  @Override
  public void toJson(JsonWriter writer) throws IOException { // FIXME: finish 2018 conversion
    writer.beginObject();
    writer.name("type").value(TYPE);
    writer.name("baseId").value(talon.getBaseID());
    writer.name("deviceId").value(talon.getDeviceID());
    writer.name("description").value("Talon " + talon.getDeviceID());
    writer.name("firmwareVersion").value(talon.getFirmwareVersion());
    writer.name("controlMode").value(talon.getControlMode().toString());
    // writer.name("brakeEnabledDuringNeutral").value(talon.getBrakeEnableDuringNeutral());
    writer
        .name("onBootBrakeMode")
        .value(talon.configGetParameter(ParamEnum.eOnBoot_BrakeMode, 0, 0));
    writer.name("busVoltage").value(talon.getBusVoltage());
    writer
        .name("feedbackSensorType")
        .value(talon.configGetParameter(ParamEnum.eFeedbackSensorType, 0, 0));
    writer
        .name("peakCurrentLimitMs")
        .value(talon.configGetParameter(ParamEnum.ePeakCurrentLimitMs, 0, 0));
    writer
        .name("peakCurrentLimitAmps")
        .value(talon.configGetParameter(ParamEnum.ePeakCurrentLimitAmps, 0, 0));

    // writer.name("encoderCodesPerRef").value(NA);
    writer.name("inverted").value(talon.getInverted());
    // writer.name("numberOfQuadIdxRises").value(talon.getNumberOfQuadIdxRises());
    writer
        .name("eQuadIdxPolarity")
        .value(talon.configGetParameter(ParamEnum.eQuadIdxPolarity, 0, 0));
    writer.name("outputVoltage").value(talon.getMotorOutputVoltage());
    writer.name("outputCurrent").value(talon.getOutputCurrent());

    writer.name("analogInput");
    writer.beginObject();
    writer.name("position").value(talon.getSensorCollection().getAnalogIn());
    writer.name("velocity").value(talon.getSensorCollection().getAnalogInVel());
    writer.name("raw").value(talon.getSensorCollection().getAnalogInRaw());
    writer.endObject();

    writer.name("encoder");
    writer.beginObject();
    writer.name("position").value(talon.getSelectedSensorPosition(0));
    writer.name("velocity").value(talon.getSelectedSensorVelocity(0));
    writer.endObject();

    writer.name("quadrature");
    writer.beginObject();
    writer.name("position").value(talon.getSensorCollection().getQuadraturePosition());
    writer.name("velocity").value(talon.getSensorCollection().getQuadratureVelocity());
    writer.endObject();

    writer.name("pulseWidth");
    writer.beginObject();
    writer.name("position").value(talon.getSensorCollection().getPulseWidthPosition());
    writer.name("velocity").value(talon.getSensorCollection().getPulseWidthVelocity());
    writer.name("riseToFallUs").value(talon.getSensorCollection().getPulseWidthRiseToFallUs());
    writer.name("riseToRiseUs").value(talon.getSensorCollection().getPulseWidthRiseToRiseUs());
    writer.endObject();

    writer.name("closedLoop");
    writer.beginObject();

    writer.name("enabled").value(true);
    writer.name("p").value(talon.configGetParameter(ParamEnum.eProfileParamSlot_P, 0, 0));
    writer.name("i").value(talon.configGetParameter(ParamEnum.eProfileParamSlot_I, 0, 0));
    writer.name("d").value(talon.configGetParameter(ParamEnum.eProfileParamSlot_D, 0, 0));
    writer.name("f").value(talon.configGetParameter(ParamEnum.eProfileParamSlot_F, 0, 0));
    writer
        .name("iAccum")
        .value(talon.configGetParameter(ParamEnum.eProfileParamSlot_MaxIAccum, 0, 0));
    writer.name("iZone").value(talon.configGetParameter(ParamEnum.eProfileParamSlot_IZone, 0, 0));
    writer.name("errorInt").value(talon.getClosedLoopError(0));
    writer.name("errorDouble").value(talon.getErrorDerivative(0));
    writer.name("rampRate").value(talon.configGetParameter(ParamEnum.eOpenloopRamp, 0, 0));
    writer.name("nominalVoltage").value(talon.configGetParameter(ParamEnum.eClosedloopRamp, 0, 0));
    writer.endObject();

    writer.name("motionMagic");
    writer.beginObject();
    if (talon.getControlMode() == ControlMode.MotionMagic) {
      writer.name("enabled").value(true);
      writer.name("acceleration").value(talon.configGetParameter(ParamEnum.eMotMag_Accel, 0, 0));
      // writer.name("actTrajPosition").value(talon.getMotionMagicActTrajPosition());
      // writer.name("actTrajVelocity").value(talon.getMotionMagicActTrajVelocity());
      writer
          .name("cruiseVelocity")
          .value(talon.configGetParameter(ParamEnum.eMotMag_VelCruise, 0, 0));
    } else {
      writer.name("enabled").value(false);
    }
    writer.endObject();

    writer.name("motionProfile");
    writer.beginObject();
    if (talon.getControlMode() == ControlMode.MotionProfile) {
      writer.name("enabled").value(true);
      writer.name("topLevelBufferCount").value(talon.getMotionProfileTopLevelBufferCount());
    } else {
      writer.name("enabled").value(false);
    }
    writer.endObject();

    writer.name("forwardSoftLimit");
    writer.beginObject();
    writer.name("enabled").value(talon.configGetParameter(ParamEnum.eForwardSoftLimitEnable, 0, 0));
    writer
        .name("limit")
        .value(talon.configGetParameter(ParamEnum.eForwardSoftLimitThreshold, 0, 0));
    writer.endObject();

    writer.name("reverseSoftLimit");
    writer.beginObject();
    writer.name("enabled").value(talon.configGetParameter(ParamEnum.eReverseSoftLimitEnable, 0, 0));
    writer
        .name("limit")
        .value(talon.configGetParameter(ParamEnum.eReverseSoftLimitThreshold, 0, 0));
    writer.endObject();

    writer.name("lastError").value(talon.getLastError().toString());
    writer.name("faults");
    writer.beginObject();
    StickyFaults stickyfaults = new StickyFaults();
    Faults faults = new Faults();
    talon.getStickyFaults(stickyfaults);
    talon.getFaults(faults);
    writer.name("lim").value(faults.ForwardLimitSwitch);
    writer.name("stickyLim").value(stickyfaults.ForwardLimitSwitch);
    writer.name("softLim").value(faults.ForwardSoftLimit);
    writer.name("stickySoftLim").value(stickyfaults.ForwardSoftLimit);
    writer.name("hardwareFailure").value(faults.HardwareFailure);
    writer.name("overTemp").value(faults.SensorOverflow);
    writer.name("stickyOverTemp").value(stickyfaults.SensorOverflow);
    writer.name("revLim").value(faults.ReverseLimitSwitch);
    writer.name("stickyRevLim").value(stickyfaults.ReverseLimitSwitch);
    writer.name("revSoftLim").value(faults.ReverseSoftLimit);
    writer.name("stickyRevSoftLim").value(stickyfaults.ReverseSoftLimit);
    writer.name("underVoltage").value(faults.UnderVoltage);
    writer.name("stickyUnderVoltage").value(stickyfaults.UnderVoltage);
    writer.endObject();

    writer.endObject();
  }

  /**
   * Indicates if some other {@code TalonItem} has the same underlying Talon as this one.
   *
   * @param obj the reference object with which to compare.
   * @return true if this TalonSRX has the same device ID, false otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof TalonItem)) {
      return false;
    }
    TalonItem item = (TalonItem) obj;
    return item.talon.getDeviceID() == talon.getDeviceID();
  }

  /**
   * Returns a hashcode value for this TalonItem.
   *
   * @return a hashcode value for this TalonItem.
   */
  @Override
  public int hashCode() {
    return talon.getDeviceID();
  }
}
