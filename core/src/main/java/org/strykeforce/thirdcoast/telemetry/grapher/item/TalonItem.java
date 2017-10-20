package org.strykeforce.thirdcoast.telemetry.grapher.item;

import com.ctre.CANTalon;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.DoubleSupplier;
import org.strykeforce.thirdcoast.telemetry.grapher.Measure;

/**
 * TalonSRX specialization for {@link Item}.
 */
public class TalonItem extends AbstractItem {

  public final static String TYPE = "talon";
  public final static Set<Measure> MEASURES = Collections.unmodifiableSet(EnumSet.of(
      Measure.SETPOINT,
      Measure.OUTPUT_CURRENT,
      Measure.OUTPUT_VOLTAGE,
      Measure.ENCODER_POSITION,
      Measure.ENCODER_VELOCITY,
      Measure.ABSOLUTE_ENCODER_POSITION,
      Measure.CONTROL_LOOP_ERROR,
      Measure.INTEGRATOR_ACCUMULATOR,
      Measure.BUS_VOLTAGE,
      Measure.FORWARD_HARD_LIMIT_CLOSED,
      Measure.REVERSE_HARD_LIMIT_CLOSED,
      Measure.FORWARD_SOFT_LIMIT_OK,
      Measure.REVERSE_SOFT_LIMIT_OK
  ));

  private final CANTalon talon;

  public TalonItem(final CANTalon talon) {
    super(TYPE, talon.getDescription(), MEASURES);
    this.talon = talon;
  }

  @Override
  public int id() {
    return talon.getDeviceID();
  }

  @Override
  public DoubleSupplier measurementFor(final Measure measure) {
    if (!MEASURES.contains(measure)) {
      throw new IllegalArgumentException("invalid measure: " + measure.name());
    }

    switch (measure) {
      case SETPOINT:
        return () -> talon.get();
      case OUTPUT_CURRENT:
        return () -> talon.getOutputCurrent();
      case OUTPUT_VOLTAGE:
        return () -> talon.getOutputVoltage();
      case ENCODER_POSITION:
        return () -> talon.getEncPosition();
      case ENCODER_VELOCITY:
        return () -> talon.getEncVelocity();
      case ABSOLUTE_ENCODER_POSITION:
        return () -> talon.getPulseWidthPosition();
      case CONTROL_LOOP_ERROR:
        return () -> talon.getClosedLoopError();
      case INTEGRATOR_ACCUMULATOR:
        return () -> talon.GetIaccum();
      case BUS_VOLTAGE:
        return () -> talon.getBusVoltage();
      case FORWARD_HARD_LIMIT_CLOSED:
        return () -> talon.isFwdLimitSwitchClosed() ? 1.0 : 0.0;
      case REVERSE_HARD_LIMIT_CLOSED:
        return () -> talon.isRevLimitSwitchClosed() ? 1.0 : 0.0;
      case FORWARD_SOFT_LIMIT_OK:
        // TODO: verify soft limit
        return () -> talon.getForwardSoftLimit();
      case REVERSE_SOFT_LIMIT_OK:
        return () -> talon.getReverseSoftLimit();
    }
    return () -> Double.NaN;

  }

  @Override
  public void toJson(JsonWriter writer) throws IOException {
    Json json = new Json(talon);
    json.toJson(writer);
  }

  @Override
  public String toString() {
    return "TalonItem{" +
        "talon=" + talon +
        "} " + super.toString();
  }

  static class Json {

    final String type = TalonItem.TYPE;
    final int id;
    final String feedbackDevice;
    final int currentLimit;
    final int encoderCodesPerRef;
    final int analogInPosition;
    final int analogInRaw;
    final int analogInVelocity;
    final boolean brakeEnabledDuringNeutral;
    final double busVoltage;
    final int closedLoopError;
    final double closeLoopRampRate;
    final String controlMode;
    final double d;
    final String description;
    final int encPosition;
    final int encVelocity;
    final double error;
    final double expiration;
    final double f;
    final long firmwareVersion;
    final int forwardSoftLimit;
    final double i;
    final double iAccum;
    final boolean inverted;
    final String lastError;
    final double motionMagicAcceleration;
    final double motionMagicActTrajPosition;
    final double motionMagicActTrajVelocity;
    final double motionMagicCruiseVelocity;
    // TODO: getMotionProfileStatus
    final int motionProfileTopLevelBufferCount;
    final double 	nominalClosedLoopVoltage;
    final int numberOfQuadIdxRises;
    final double outputCurrent;
    final double outputVoltage;
    final double p;

    final Map<String, Integer> faults = new HashMap<>(6);

    public Json(CANTalon t) {
      id = t.getDeviceID();
      controlMode = t.getControlMode().toString();
      feedbackDevice = "unknown";
      currentLimit = -1;
      encoderCodesPerRef = -1;
      analogInPosition = t.getAnalogInPosition();
      analogInRaw = t.getAnalogInRaw();
      analogInVelocity = t.getAnalogInVelocity();
      brakeEnabledDuringNeutral = t.getBrakeEnableDuringNeutral();
      busVoltage = t.getBusVoltage();
      closedLoopError = t.getClosedLoopError();
      closeLoopRampRate = t.getCloseLoopRampRate();
      d = t.getD();
      description = t.getDescription();
      encPosition = t.getEncPosition();
      encVelocity = t.getEncVelocity();
      error = t.getError();
      expiration = t.getExpiration();
      f = t.getF();
      firmwareVersion = t.GetFirmwareVersion();
      forwardSoftLimit = t.getForwardSoftLimit();
      i = t.getI();
      iAccum = t.GetIaccum();
      inverted = t.getInverted();
      lastError = t.getLastError();
      motionMagicAcceleration = t.getMotionMagicAcceleration();
      motionMagicActTrajPosition = t.getMotionMagicActTrajPosition();
      motionMagicActTrajVelocity = t.getMotionMagicActTrajVelocity();
      motionMagicCruiseVelocity = t.getMotionMagicCruiseVelocity();
      motionProfileTopLevelBufferCount = t.getMotionProfileTopLevelBufferCount();
      nominalClosedLoopVoltage = t.GetNominalClosedLoopVoltage();
      numberOfQuadIdxRises = t.getNumberOfQuadIdxRises();
      outputCurrent = t.getOutputCurrent();
      outputVoltage = t.getOutputVoltage();
      p = t.getP();

      faults.put("lim", t.getFaultForLim());
      faults.put("softLim", t.getFaultForSoftLim());
      faults.put("hardwareFailure", t.getFaultHardwareFailure());
      faults.put("overTemp", t.getFaultOverTemp());
      faults.put("revLim", t.getFaultRevLim());
      faults.put("revSoftLim", t.getFaultRevSoftLim());
      faults.put("underVoltage", t.getFaultUnderVoltage());

    }

    public void toJson(JsonWriter writer) throws IOException {
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<Json> adapter = moshi.adapter(Json.class);
      adapter.toJson(writer, Json.this);
    }

  }
}
