package org.strykeforce.telemetry.grapher;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.Set;
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
import org.strykeforce.telemetry.Inventory;
import org.strykeforce.telemetry.measurable.Measurable;
import org.strykeforce.telemetry.measurable.Measure;

@ExtendWith(MockitoExtension.class)
class SubscriptionTest {

  @Mock Measurable itemZero, itemOne;
  @Mock Inventory inventory;

  Measure baseId = new Measure("BASE_ID", "BASE_ID", () -> 27.0);
  Measure value = new Measure("VALUE", "VALUE", () -> 67.0);
  Measure jerkExpected = new Measure("JERK_EXPECTED", "JERK_EXPECTED", () -> 2767.0);
  Set<Measure> measures = Set.of(baseId, value, jerkExpected);

  @BeforeEach
  void setUp() {
    doReturn(itemZero).when(inventory).measurableForId(0);
    doReturn(itemOne).when(inventory).measurableForId(1);

    doReturn(measures).when(itemZero).getMeasures();
    doReturn(measures).when(itemOne).getMeasures();

    when(itemZero.getDescription()).thenReturn("item zero");
    when(itemOne.getDescription()).thenReturn("item one");
  }

  @Test
  void measurementToJson() throws IOException, JSONException {

    Subscription subscription =
        new Subscription(
            inventory,
            InetAddress.getByName("localhost"),
            ResourceHelper.getString("/request.json"));

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
        new Subscription(
            inventory,
            InetAddress.getByName("localhost"),
            ResourceHelper.getString("/request.json"));

    Buffer buffer = new Buffer();
    subscription.toJson(buffer);
    JSONObject actual =
        (JSONObject) JSONParser.parseJSON(buffer.readString(Charset.defaultCharset()));
    actual.put("timestamp", 2767);

    JSONAssert.assertEquals(ResourceHelper.getString(
        "/telemetry-controller-subscription-response.json"), actual, true);
  }
}
