package org.strykeforce.thirdcoast.telemetry.grapher;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.function.DoubleSupplier;
import okio.Buffer;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONParser;
import org.strykeforce.thirdcoast.telemetry.Inventory;
import org.strykeforce.thirdcoast.telemetry.item.Measurable;
import org.strykeforce.thirdcoast.telemetry.item.Measure;

@ExtendWith(MockitoExtension.class)
class SubscriptionTest {

  @Mock Measurable itemZero, itemOne;
  @Mock Inventory inventory;

  @BeforeEach
  void setUp() {
    doReturn(itemZero).when(inventory).itemForId(0);
    doReturn(itemOne).when(inventory).itemForId(1);
    doReturn((DoubleSupplier) () -> 27d)
        .when(itemZero)
        .measurementFor(new Measure("BASE_ID", "BASE_ID"));
    doReturn((DoubleSupplier) () -> 67d)
        .when(itemZero)
        .measurementFor(new Measure("VALUE", "VALUE"));
    doReturn((DoubleSupplier) () -> 2767d)
        .when(itemOne)
        .measurementFor(new Measure("JERK_EXPECTED", "JERK_EXPECTED"));
    when(itemZero.getDescription()).thenReturn("item zero");
    when(itemOne.getDescription()).thenReturn("item one");
  }

  @Test
  void measurementToJson() throws IOException, JSONException {

    Subscription subscription =
        new Subscription(inventory, "unit tests", ResourceHelper.getString("/request.json"));

    Buffer buffer = new Buffer();
    subscription.measurementsToJson(buffer);
    JSONObject actual =
        (JSONObject) JSONParser.parseJSON(buffer.readString(Charset.defaultCharset()));
    actual.put("timestamp", 2767);

    JSONAssert.assertEquals("{\"timestamp\":2767,\"data\":[27.0,67.0,2767.0]}", actual, true);
  }

  @Test
  void toJson() throws IOException, JSONException {
    Subscription subscription =
        new Subscription(inventory, "unit tests", ResourceHelper.getString("/request.json"));

    Buffer buffer = new Buffer();
    subscription.toJson(buffer);
    JSONObject actual =
        (JSONObject) JSONParser.parseJSON(buffer.readString(Charset.defaultCharset()));
    actual.put("timestamp", 2767);

    JSONAssert.assertEquals(ResourceHelper.getString("/subscription.json"), actual, true);
  }
}
