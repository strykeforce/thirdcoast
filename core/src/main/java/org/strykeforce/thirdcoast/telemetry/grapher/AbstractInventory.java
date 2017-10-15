package org.strykeforce.thirdcoast.telemetry.grapher;

import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.*;

import com.squareup.moshi.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import okio.BufferedSink;

/**
 * An abstract base class intended to be subclassed by concrete implmentations of {@link Inventory}.
 */
public abstract class AbstractInventory implements Inventory {

  protected final List<Item> items = new ArrayList<>(16);

  public AbstractInventory(final Collection<Item> items) {
    this.items.addAll(items);
  }

  public Item itemForId(final int id) {
    return items.get(id);
  }

  public void writeInventory(BufferedSink sink) throws IOException {
    JsonWriter writer = JsonWriter.of(sink);
    writer.setIndent("  ");
    writer.beginObject();
    writer.name("items");
    writeItems(writer);
    writer.name("measures");
    writeMeasures(writer);
    writer.endObject();
  }

  void writeItems(JsonWriter writer) throws IOException {
    writer.beginArray();
    for (int i = 0; i < items.size(); i++) {
      writer.beginObject();
      writer.name("id").value(i);
      writer.name("type").value(items.get(i).type());
      writer.name("description").value(items.get(i).description());
      writer.endObject();
    }
    writer.endArray();
  }

 void writeMeasures(JsonWriter writer) throws IOException {
    writer.beginObject();
    writer.name("talon");
    writeTalonMeasures(writer);
    writer.endObject();
  }

  void writeMeasure(JsonWriter writer, Measure measure)
      throws IOException {
    writer.beginObject();
    writer.name("id").value(measure.name());
    writer.name("description").value(measure.getDescription());
    writer.endObject();

  }

  void writeTalonMeasures(JsonWriter writer) throws IOException {
    writer.beginArray();
    int i = 0;
    writeMeasure(writer, SETPOINT);
    writeMeasure(writer, OUTPUT_CURRENT);
    writeMeasure(writer, OUTPUT_VOLTAGE);
    writeMeasure(writer, ENCODER_POSITION);
    writeMeasure(writer, ENCODER_VELOCITY);
    writeMeasure(writer, ABSOLUTE_ENCODER_POSITION);
    writeMeasure(writer, CONTROL_LOOP_ERROR);
    writeMeasure(writer, INTEGRATOR_ACCUMULATOR);
    writeMeasure(writer, BUS_VOLTAGE);
    writeMeasure(writer, FORWARD_HARD_LIMIT_CLOSED);
    writeMeasure(writer, REVERSE_HARD_LIMIT_CLOSED);
    writeMeasure(writer, FORWARD_SOFT_LIMIT_OK);
    writeMeasure(writer, REVERSE_SOFT_LIMIT_OK);
    writer.endArray();
  }

  @Override
  public String toString() {
    return "AbstractInventory{" +
        "items=" + items +
        '}';
  }
}
