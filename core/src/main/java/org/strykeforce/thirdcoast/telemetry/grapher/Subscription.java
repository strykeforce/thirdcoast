package org.strykeforce.thirdcoast.telemetry.grapher;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.DoubleSupplier;
import okio.BufferedSink;
import org.strykeforce.thirdcoast.telemetry.grapher.item.Item;

/**
 * Represents a subscription request for streaming data.
 */
public class Subscription {

  private final String client;
  private final List<DoubleSupplier> measurements = new ArrayList<>(16);
  private final List<String> descriptions = new ArrayList<>(16);

  public Subscription(Inventory inventory, String client, String requestJson) {
    this.client = client;
    RequestJson request = RequestJson.EMPTY;
    try {
      request = RequestJson.fromJson(requestJson);
    } catch (IOException e) {
      e.printStackTrace();
    }

    for (RequestJson.Item jsonItem : request.subscription) {
      Item item = inventory.itemForId(jsonItem.itemId);
      Measure measure = Measure.valueOf(jsonItem.measurementId);
      measurements.add(item.measurementFor(measure));
      descriptions.add(item.description() + ": " + measure.getDescription());
    }
  }


  public String client() {
    return client;
  }

  public void measurementsToJson(BufferedSink sink) throws IOException {
    long ts = System.currentTimeMillis();
    JsonWriter writer = JsonWriter.of(sink);
    writer.beginObject();
    writer.name("timestamp").value(ts);
    writer.name("data");
    writer.beginArray();
    for (DoubleSupplier m : measurements) {
      writer.value(m.getAsDouble());
    }
    writer.endArray();
    writer.endObject();

  }

  public void toJson(BufferedSink sink) throws IOException {
    long ts = System.currentTimeMillis();
    JsonWriter writer = JsonWriter.of(sink);
    writer.beginObject();
    writer.name("type").value("subscription");
    writer.name("timestamp").value(ts);
    writer.name("descriptions");
    writer.beginArray();
    for (String d : descriptions) {
      writer.value(d);
    }
    writer.endArray();
    writer.endObject();
  }

  static class RequestJson {

    public final static RequestJson EMPTY;

    static {
      EMPTY = new RequestJson();
      EMPTY.type = "start";
      EMPTY.subscription = Collections.emptyList();
    }

    public String type;
    public List<Item> subscription;

    public static RequestJson fromJson(final String json) throws IOException {
      // TODO: verify type=start
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<RequestJson> adapter = moshi.adapter(RequestJson.class);
      return adapter.fromJson(json);
    }

    @Override
    public String toString() {
      return "RequestJson{" +
          "type='" + type + '\'' +
          ", subscription=" + subscription +
          '}';
    }

    static class Item {

      public int itemId;
      public String measurementId;

      @Override
      public String toString() {
        return "Item{" +
            "itemId=" + itemId +
            ", measurementId=" + measurementId +
            '}';
      }
    }
  }
}
