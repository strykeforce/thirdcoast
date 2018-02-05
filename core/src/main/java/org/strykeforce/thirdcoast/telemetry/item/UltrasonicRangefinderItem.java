package org.strykeforce.thirdcoast.telemetry.item;

import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.VALUE;

import com.ctre.phoenix.CANifier;
import com.ctre.phoenix.CANifier.PWMChannel;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.DoubleSupplier;
import org.strykeforce.thirdcoast.telemetry.grapher.Measure;

public class UltrasonicRangefinderItem extends AbstractItem {

  private static final Set<Measure> MEASURES = Collections.unmodifiableSet(EnumSet.of(VALUE));

  private final int deviceId;
  private final CANifier canifier;
  private final PWMChannel pwmChannel;
  private final double[] dutyCycleAndPeriod = new double[2];

  public UltrasonicRangefinderItem(int canId, PWMChannel pwmChannel) {
    super("sensor", "Ultrasonic Rangefinder (" + canId + ", " + pwmChannel + ")", MEASURES);
    this.pwmChannel = pwmChannel;
    deviceId = canId * 10 + pwmChannel.value;
    canifier = new CANifier(canId);
  }

  @Override
  public int deviceId() {
    return deviceId;
  }

  @Override
  public DoubleSupplier measurementFor(Measure measure) {
    if (!MEASURES.contains(measure)) {
      throw new IllegalArgumentException("invalid measure: " + measure.name());
    }
    return () -> {
      canifier.getPWMInput(pwmChannel, dutyCycleAndPeriod);
      return dutyCycleAndPeriod[0];
    };
  }
}
