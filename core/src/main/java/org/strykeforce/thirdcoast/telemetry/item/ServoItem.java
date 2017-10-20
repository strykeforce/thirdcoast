package org.strykeforce.thirdcoast.telemetry.item;

import edu.wpi.first.wpilibj.Servo;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.DoubleSupplier;
import org.strykeforce.thirdcoast.telemetry.grapher.Measure;

public class ServoItem extends AbstractItem {

  public final static String TYPE = "servo";
  public final static Set<Measure> MEASURES = Collections.unmodifiableSet(EnumSet.of(
      Measure.POSITION,
      Measure.ANGLE
  ));

  private final Servo servo;

  public ServoItem(Servo servo, String description) {
    super(TYPE, description, MEASURES);
    this.servo = servo;
  }

  public ServoItem(Servo servo) {
    this(servo, "Servo " + servo.getChannel());
  }

  @Override
  public int id() {
    return servo.getChannel();
  }

  @Override
  public DoubleSupplier measurementFor(Measure measure) {
    if (!MEASURES.contains(measure)) {
      throw new IllegalArgumentException("invalid measure: " + measure.name());
    }
    switch (measure) {
      case POSITION:
        return () -> servo.getPosition();
      case ANGLE:
        return () -> servo.getAngle();
        default:
          throw new IllegalStateException("should not get here");
    }
  }

  @Override
  public String toString() {
    return "ServoItem{" +
        "servo=" + servo +
        "} " + super.toString();
  }
}
