package org.strykeforce.thirdcoast.telemetry.grapher.item;

import com.squareup.moshi.JsonWriter;
import java.io.IOException;
import java.util.Set;
import java.util.function.DoubleSupplier;
import org.strykeforce.thirdcoast.telemetry.grapher.Measure;

/**
 * An item that can be graphed.
 */
public interface Item {

  int id();

  String type();

  String description();

  Set<Measure> measures();

  DoubleSupplier measurementFor(Measure measure);

  void toJson(JsonWriter writer) throws IOException;
}
