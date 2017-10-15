package org.strykeforce.thirdcoast.telemetry.grapher;

import com.ctre.CANTalon;
import java.util.function.DoubleSupplier;

/**
 * TalonSRX specialization for {@link Item}.
 */
class TalonItem implements Item {

  private final CANTalon talon;

  public TalonItem(final CANTalon talon) {
    this.talon = talon;
  }

  @Override
  public int id() {
    return talon.getDeviceID();
  }

  @Override
  public String type() {
    return "talon";
  }

  @Override
  public String description() {
    return talon.getDescription();
  }

  @Override
  public DoubleSupplier measurementFor(final Measure measure) {
    switch (measure) {
      case SETPOINT:
        return () -> talon.getSetpoint();
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
  public String toString() {
    return "TalonItem{" +
        "talon=" + talon +
        '}';
  }
}
