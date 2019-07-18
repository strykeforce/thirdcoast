package org.strykeforce.thirdcoast.telemetry.grapher;

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
import org.strykeforce.thirdcoast.telemetry.graphable.Graphable;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.function.DoubleSupplier;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionTest {

  @Mock Graphable graphableZero, graphableOne;
  @Mock Inventory inventory;

  @BeforeEach
  void setUp() {
    doReturn(graphableZero).when(inventory).graphableForId(0);
    doReturn(graphableOne).when(inventory).graphableForId(1);
    doReturn((DoubleSupplier) () -> 27d).when(graphableZero).measurementFor(BASE_ID);
    doReturn((DoubleSupplier) () -> 67d).when(graphableZero).measurementFor(VALUE);
    doReturn((DoubleSupplier) () -> 2767d).when(graphableOne).measurementFor(JERK_EXPECTED);
    when(graphableZero.getDescription()).thenReturn("graphable zero");
    when(graphableOne.getDescription()).thenReturn("graphable one");
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
