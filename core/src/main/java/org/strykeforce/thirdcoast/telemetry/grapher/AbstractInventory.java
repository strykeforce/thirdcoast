package org.strykeforce.thirdcoast.telemetry.grapher;

import com.squareup.moshi.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import okio.BufferedSink;
import org.strykeforce.thirdcoast.telemetry.grapher.Item.Type;

/**
 * An abstract base class intended to be subclassed by concrete implmentations of {@link
 * Inventory}.
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
      writer.name("type").value(items.get(i).type().name());
      writer.name("description").value(items.get(i).description());
      writer.endObject();
    }
    writer.endArray();
  }

  void writeMeasures(JsonWriter writer) throws IOException {
    Set<Type> types = items.stream().map(Item::type).collect(Collectors.toSet());
    writer.beginArray();
    for (Type type : types) {
      writeDeviceMeasures(writer, type);
    }
    writer.endArray();
  }

  void writeDeviceMeasures(JsonWriter writer, Item.Type type) throws IOException {
    writer.beginObject();
    writer.name("deviceType").value(type.name());
    writer.name("deviceMeasures");
    writer.beginArray();
    for (Measure m : type.measures()) {
      writeMeasure(writer, m);
    }
    writer.endArray();

    writer.endObject();
  }


  void writeMeasure(JsonWriter writer, Measure measure)
      throws IOException {
    writer.beginObject();
    writer.name("id").value(measure.name());
    writer.name("description").value(measure.getDescription());
    writer.endObject();

  }

  @Override
  public String toString() {
    return "AbstractInventory{" +
        "items=" + items +
        '}';
  }
}
