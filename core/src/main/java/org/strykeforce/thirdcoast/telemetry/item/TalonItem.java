package org.strykeforce.thirdcoast.telemetry.item;

import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
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
              Measure.SETPOINT,
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

  public TalonItem(final TalonSRX talon) {
    super(TYPE, ((WPI_TalonSRX) talon).getDescription(), MEASURES);
    this.talon = talon;
    sensorCollection = talon.getSensorCollection();
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

    // FIXME: FIXME FIXME FIXME
    switch (measure) {
        // TODO: should be coming in CTRE update
        //      case SETPOINT:
        //        return talon::getSetpoint;
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
        return sensorCollection::getPulseWidthPosition;
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
    writer.name("deviceId").value(talon.getDeviceID());
    writer.name("description").value(((WPI_TalonSRX) talon).getDescription());
    writer.name("firmwareVersion").value(talon.getFirmwareVersion());
    writer.name("controlMode").value(talon.getControlMode().toString());
    //    writer.name("brakeEnabledDuringNeutral").value(talon.getBrakeEnableDuringNeutral());
    writer.name("busVoltage").value(talon.getBusVoltage());
    writer.name("feedbackDevice").value(NA);
    writer.name("currentLimit").value(NA);
    writer.name("encoderCodesPerRef").value(NA);
    writer.name("inverted").value(talon.getInverted());
    //    writer.name("numberOfQuadIdxRises").value(talon.getNumberOfQuadIdxRises());
    //    writer.name("outputVoltage").value(talon.getOutputVoltage());
    writer.name("outputCurrent").value(talon.getOutputCurrent());

    writer.name("analogInput");
    writer.beginObject();
    //    writer.name("position").value(talon.getAnalogInPosition());
    //    writer.name("velocity").value(talon.getAnalogInVelocity());
    //    writer.name("raw").value(talon.getAnalogInRaw());
    writer.endObject();

    writer.name("encoder");
    writer.beginObject();
    //    writer.name("position").value(talon.getEncPosition());
    //    writer.name("velocity").value(talon.getEncVelocity());
    writer.endObject();

    writer.name("closedLoop");
    writer.beginObject();
    //    if (CLOSED_LOOP.contains(talon.getControlMode())) {
    //      writer.name("enabled").value(true);
    //      writer.name("p").value(talon.getP());
    //      writer.name("i").value(talon.getI());
    //      writer.name("d").value(talon.getD());
    //      writer.name("f").value(talon.getF());
    //      writer.name("iAccum").value(talon.GetIaccum());
    //      writer.name("iZone").value(talon.getIZone());
    //      writer.name("errorInt").value(talon.getClosedLoopError());
    //      writer.name("errorDouble").value(talon.getError());
    //      writer.name("rampRate").value(talon.getCloseLoopRampRate());
    //      writer.name("nominalVoltage").value(talon.GetNominalClosedLoopVoltage());
    //    } else {
    //      writer.name("enabled").value(false);
    //    }
    writer.endObject();

    writer.name("motionMagic");
    writer.beginObject();
    //    if (talon.getControlMode() == TalonControlMode.MotionMagic) {
    //      writer.name("enabled").value(true);
    //      writer.name("acceleration").value(talon.getMotionMagicAcceleration());
    //      writer.name("actTrajPosition").value(talon.getMotionMagicActTrajPosition());
    //      writer.name("actTrajVelocity").value(talon.getMotionMagicActTrajVelocity());
    //      writer.name("cruiseVelocity").value(talon.getMotionMagicCruiseVelocity());
    //    } else {
    //      writer.name("enabled").value(false);
    //    }
    writer.endObject();

    writer.name("motionProfile");
    writer.beginObject();
    //    if (talon.getControlMode() == TalonControlMode.MotionProfile) {
    //      writer.name("enabled").value(true);
    //      writer.name("topLevelBufferCount").value(talon.getMotionProfileTopLevelBufferCount());
    //    } else {
    //      writer.name("enabled").value(false);
    //    }
    writer.endObject();

    writer.name("forwardSoftLimit");
    writer.beginObject();
    //    writer.name("enabled").value(talon.isForwardSoftLimitEnabled());
    //    if (talon.isForwardSoftLimitEnabled()) {
    //      writer.name("limit").value(talon.getForwardSoftLimit());
    //    }
    writer.endObject();

    writer.name("reverseSoftLimit");
    writer.beginObject();
    //    writer.name("enabled").value(talon.isReverseSoftLimitEnabled());
    //    if (talon.isReverseSoftLimitEnabled()) {
    //      writer.name("limit").value(talon.getReverseSoftLimit());
    //    }
    writer.endObject();

    //    writer.name("lastError").value(talon.getLastError());
    //    writer.name("faults");
    //    writer.beginObject();
    //    writer.name("lim").value(talon.getFaultForLim());
    //    writer.name("stickyLim").value(talon.getStickyFaultForLim());
    //    writer.name("softLim").value(talon.getFaultForSoftLim());
    //    writer.name("stickySoftLim").value(talon.getStickyFaultForSoftLim());
    //    writer.name("hardwareFailure").value(talon.getFaultHardwareFailure());
    //    writer.name("overTemp").value(talon.getFaultOverTemp());
    //    writer.name("stickyOverTemp").value(talon.getStickyFaultOverTemp());
    //    writer.name("revLim").value(talon.getFaultRevLim());
    //    writer.name("stickyRevLim").value(talon.getStickyFaultRevLim());
    //    writer.name("revSoftLim").value(talon.getFaultRevSoftLim());
    //    writer.name("stickyRevSoftLim").value(talon.getStickyFaultRevSoftLim());
    //    writer.name("underVoltage").value(talon.getFaultUnderVoltage());
    //    writer.name("stickyUnderVoltage").value(talon.getStickyFaultUnderVoltage());
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
