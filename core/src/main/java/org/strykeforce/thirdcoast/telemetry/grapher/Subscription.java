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
import org.strykeforce.thirdcoast.telemetry.grapher.Item.Measure;

public class Subscription {

  private final String client;
  private final List<DoubleSupplier> measurements = new ArrayList<>(16);

  FakeGraphData message = FakeGraphData.sineWaves();

  public Subscription(Inventory inventory, String client, String json) {
    this.client = client;
    RequestJson request = RequestJson.EMPTY;
    try {
      request = RequestJson.fromJson(json);
    } catch (IOException e) {
      e.printStackTrace();
    }

    for (RequestJson.Item jsonItem: request.subscription) {
      Item item = inventory.itemForId(jsonItem.itemId);
      Measure measure = Measure.findByJsonId(jsonItem.measurementId);
      measurements.add(item.measurementFor(measure));
    }
  }


  public String client() {
    return client;
  }

  public void toJson(BufferedSink sink) throws IOException {
//    message.toJson(sink);
    long ts = System.currentTimeMillis();
    JsonWriter writer = JsonWriter.of(sink);
//    writer.setIndent("  ");
    writer.beginObject();
    writer.name("type").value("talon");
    writer.name("timestamp").value(ts);
    writer.name("data");
    writer.beginArray();
    for (DoubleSupplier m : measurements) {
      writer.value(m.getAsDouble());
    }
    writer.endArray();
    writer.endObject();

  }

  public static class RequestJson {

    public final static RequestJson EMPTY;

    static {
      EMPTY = new RequestJson();
      EMPTY.type = "start";
      EMPTY.subscription = Collections.emptyList();
    }

    public String type;
    public List<Item> subscription;

    public static RequestJson fromJson(String json) throws IOException {
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

    public static class Item {

      public int itemId;
      public int measurementId;

      @Override
      public String toString() {
        return "Item{" +
            "itemId=" + itemId +
            ", measurementId=" + measurementId +
            '}';
      }
    }
  }


  public static class FakeGraphData {

    private final static int TALON_MAX = 8;
    private static double x = 0;

    public double[] data;

    public FakeGraphData(double[] data) {
      this.data = data;
    }

    public static FakeGraphData sineWaves() {
      x += 0.01;

      double[] data = new double[TALON_MAX];
      for (int i = 0; i < TALON_MAX; i++) {
        data[i] = 10 * Math.sin((i + 1) * x + i);
      }
      return new FakeGraphData(data);
    }

    public void toJson(BufferedSink buffer) throws IOException {
      long ts = System.currentTimeMillis();
      JsonWriter writer = JsonWriter.of(buffer);
      writer.setIndent("  ");
      writer.beginObject();
      writer.name("type").value("talon");
      writer.name("timestamp").value(ts);
      writer.name("data");
      writer.beginArray();
      for (double d : data) {
        writer.value(d);
      }
      writer.endArray();
      writer.endObject();
    }

  }

}
