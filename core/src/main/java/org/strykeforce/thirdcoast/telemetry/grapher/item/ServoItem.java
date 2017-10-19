package org.strykeforce.thirdcoast.telemetry.grapher.item;

import edu.wpi.first.wpilibj.Servo;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.DoubleSupplier;
import org.strykeforce.thirdcoast.telemetry.grapher.Measure;

public class ServoItem implements Item {

  public final static String TYPE = "servo";
  public final static Set<Measure> MEASURES = Collections.unmodifiableSet(EnumSet.of(
      Measure.POSITION,
      Measure.ANGLE
  ));

  private final Servo servo;
  private final String description;

  public ServoItem(Servo servo, String description) {
    this.servo = servo;
    this.description = description;
  }

  public ServoItem(Servo servo) {
    this(servo, "Servo " + servo.getChannel());
  }

  @Override
  public int id() {
    return servo.getChannel();
  }

  @Override
  public String type() {
    return TYPE;
  }

  @Override
  public String description() {
    return description;
  }

  @Override
  public Set<Measure> measures() {
    return MEASURES;
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
}
