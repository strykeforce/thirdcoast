package org.strykeforce.telemetry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Collections;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.JSONAssert;
import org.strykeforce.telemetry.grapher.ClientHandler;
import org.strykeforce.telemetry.grapher.ResourceHelper;
import org.strykeforce.telemetry.measurable.Measurable;
import org.strykeforce.telemetry.measurable.Measure;

@ExtendWith(MockitoExtension.class)
public class TelemetryControllerTest {

  Measurable measurable = new TestMeasurable();
  Inventory inventory = new RobotInventory(Collections.singleton(measurable));
  @Mock
  ClientHandler clientHandler;
  TelemetryController telemetryController;
  int port;

  @BeforeEach
  void setUp() {
    InetSocketAddress socket = null;
    try {
      port = getFreePort();
      socket = new InetSocketAddress(port);
    } catch (IOException e) {
      fail(e);
    }
    telemetryController = new TelemetryController(inventory, clientHandler, socket);
  }

  @Test
  @DisplayName("Should get inventory after restart")
  void shouldGetInventoryAfterRestart() throws URISyntaxException {
    telemetryController.start();
    telemetryController.shutdown();
    telemetryController.start();

    var client = HttpClient.newHttpClient();
    var uri = new URI("http", null, "127.0.0.1", port,
        "/v1/grapher/inventory", null, null);
    var request = HttpRequest.newBuilder(uri).GET().build();
    try {
      HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
      var expected = ResourceHelper.getString("/telemetry-controller-inventory.json");
      JSONAssert.assertEquals(expected, response.body(), false);
    } catch (Exception e) {
      fail(e);
    } finally {
      telemetryController.shutdown();
    }
  }

  @Test
  @DisplayName("Should handle subscription request")
  void shouldHandleSubscriptionRequest() throws URISyntaxException {
    telemetryController.start();
    var client = HttpClient.newHttpClient();
    var uri = new URI("http", null, "127.0.0.1", port,
        "/v1/grapher/subscription", null, null);
    var subReq = ResourceHelper.getString("/telemetry-controller-subscription.json");
    var request = HttpRequest.newBuilder(uri).header("Content-Type", "application/json")
        .POST(BodyPublishers.ofString(subReq)).build();

    try {
      HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
      var expected = ResourceHelper.getString("/telemetry-controller-subscription-response.json");
      JSONAssert.assertEquals(expected, response.body(), false);
    } catch (Exception e) {
      fail(e);
    } finally {
      telemetryController.shutdown();
    }
  }

  @Test
  @DisplayName("Should throw when started twice")
  void shouldThrowWhenStartedTwice() {
    InetSocketAddress socket = null;
    try {
      socket = new InetSocketAddress(getFreePort());
    } catch (IOException e) {
      fail(e);
    }
    var telemetryController = new TelemetryController(inventory, clientHandler, socket);
    telemetryController.start();
    assertThrows(IllegalStateException.class, telemetryController::start);
  }

  private int getFreePort() throws IOException {
    try (ServerSocket socket = new ServerSocket(0)) {
      socket.setReuseAddress(true);
      return socket.getLocalPort();
    }
  }

  static class TestMeasurable implements Measurable {

    @Override
    public int getDeviceId() {
      return 0;
    }

    @NotNull
    @Override
    public String getDescription() {
      return "Test Measurable";
    }

    @NotNull
    @Override
    public Set<Measure> getMeasures() {
      return Set.of(new Measure("Measure A", () -> 2767.0));
    }
  }
}
