package org.strykeforce.thirdcoast.telemetry;

import com.squareup.moshi.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import okio.BufferedSink;
import org.strykeforce.thirdcoast.telemetry.grapher.Measure;
import org.strykeforce.thirdcoast.telemetry.item.Item;

/**
 * An abstract base class intended to be subclassed by concrete implmentations of {@link Inventory}.
 */
public abstract class AbstractInventory implements Inventory {

  protected final List<Item> items = new ArrayList<>(16);

  public AbstractInventory(final Collection<Item> items) {
    this.items.addAll(items);
    Collections.sort(this.items);
  }

  @Override
  public Item itemForId(final int id) {
    return items.get(id);
  }

  @Override
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
    Map<String, Set<Measure>> measures = new HashMap<>();
    items.forEach(it -> measures.putIfAbsent(it.type(), it.measures()));
    writer.beginArray();
    for (Entry<String, Set<Measure>> entry : measures.entrySet()) {
      writeDeviceMeasures(writer, entry.getKey(), entry.getValue());
    }
    writer.endArray();
  }

  void writeDeviceMeasures(JsonWriter writer, String type, Set<Measure> measures)
      throws IOException {
    writer.beginObject();
    writer.name("deviceType").value(type);
    writer.name("deviceMeasures");
    writer.beginArray();
    for (Measure m : measures) {
      writeMeasure(writer, m);
    }
    writer.endArray();
    writer.endObject();
  }

  void writeMeasure(JsonWriter writer, Measure measure) throws IOException {
    writer.beginObject();
    writer.name("id").value(measure.name());
    writer.name("description").value(measure.getDescription());
    writer.endObject();
  }

  @Override
  public void toJson(BufferedSink sink) throws IOException {
    JsonWriter writer = JsonWriter.of(sink);
    writer.setIndent("  ");
    writer.beginArray();
    for (Item item : items) {
      item.toJson(writer);
    }
    writer.endArray();
  }

  @Override
  public String toString() {
    return "AbstractInventory{" + "items=" + items + '}';
  }
}
