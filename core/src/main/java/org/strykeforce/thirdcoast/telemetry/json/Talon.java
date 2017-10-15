package org.strykeforce.thirdcoast.telemetry.json;

import com.ctre.CANTalon;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okio.BufferedSink;

/**
 * Wraps a {@link CANTalon} and presents a JSON representation.
 */
public class Talon {

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

  final Map<String, Integer> faults = new HashMap<>(6);

  public Talon(CANTalon t) {
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

    faults.put("lim", t.getFaultForLim());
    faults.put("softLim", t.getFaultForSoftLim());
    faults.put("hardwareFailure", t.getFaultHardwareFailure());
    faults.put("overTemp", t.getFaultOverTemp());
    faults.put("revLim", t.getFaultRevLim());
    faults.put("revSoftLim", t.getFaultRevSoftLim());
    faults.put("underVoltage", t.getFaultUnderVoltage());

  }

  public void toJson(BufferedSink sink) throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Talon> adapter = moshi.adapter(Talon.class);
    adapter = adapter.indent("  ");
    adapter.toJson(sink, this);
  }

}
