package org.strykeforce.thirdcoast.swerve;

import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.ANGLE;
import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.GYRO_YAW;
import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.GYRO_YAW_RATE;

import com.kauailabs.navx.frc.AHRS;
import com.squareup.moshi.JsonWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.DoubleSupplier;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.grapher.Measure;
import org.strykeforce.thirdcoast.telemetry.item.Item;
import org.strykeforce.thirdcoast.util.Settings;

public class GraphableSwerveDrive extends SwerveDrive implements Item {

  private static final String TYPE = "swerve";
  private static final String DESCRIPTION = "Swerve Drive";

  private static final Set<Measure> MEASURES =
      Collections.unmodifiableSet(EnumSet.of(ANGLE, GYRO_YAW, GYRO_YAW_RATE));

  private double prevAngle;

  @Inject
  public GraphableSwerveDrive(AHRS gyro, Wheel[] wheels, Settings settings) {
    super(gyro, wheels, settings);
  }

  @Override
  public void registerWith(TelemetryService telemetryService) {
    super.registerWith(telemetryService);
    telemetryService.register(this);
  }

  @Override
  public int deviceId() {
    return 0;
  }

  @Override
  public String type() {
    return TYPE;
  }

  @Override
  public String description() {
    return DESCRIPTION;
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
      case ANGLE:
        return gyro::getAngle;
      case GYRO_YAW:
        return gyro::getYaw;
      case GYRO_YAW_RATE:
        return gyro::getRate;
      default:
        throw new AssertionError(measure);
    }
  }

  @Override
  public void toJson(JsonWriter writer) throws IOException {
    writer.beginObject();
    writer.name("not").value("implemented");
    writer.endObject();
  }

  @Override
  public int compareTo(@NotNull Item o) {
    return 0;
  }
}
