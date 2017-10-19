package org.strykeforce.thirdcoast.telemetry.grapher.item;

import com.ctre.CANTalon;
import java.util.Collections;
import java.util.EnumSet;
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
        "} " + super.toString();
  }
}
