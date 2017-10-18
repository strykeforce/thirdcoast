package org.strykeforce.thirdcoast.telemetry.grapher;

import com.squareup.moshi.JsonWriter;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import okio.Buffer;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.request.Method;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;

/**
 * Provides a development web service to control telemetry.
 */
public class GrapherController extends NanoHTTPD {

  private final static String JSON = "application/json";

  private final Inventory inventory;
  private final ClientHandler clientHandler;
  private final int port;

  @Inject
  public GrapherController(final Inventory inventory, final ClientHandler clientHandler,
      final @Named("server") int port) {
    super(port);
    this.inventory = inventory;
    this.clientHandler = clientHandler;
    this.port = port;

    addHTTPInterceptor(session -> {
//      double start = Timer.getFPGATimestamp();
      if (session.getMethod() == Method.GET && session.getUri()
          .equalsIgnoreCase("/v1/grapher/inventory")) {
        Buffer buffer = new Buffer();
        try {
          inventory.writeInventory(buffer);
        } catch (IOException e) {
          e.printStackTrace();
          return errorResponseFor(e);
        }
//        System.out.printf("start response %g%n", (Timer.getFPGATimestamp() - start) * 1000);
        return Response
            .newFixedLengthResponse(Status.OK, JSON, buffer.readByteArray());
      }
      return null;
    });

    addHTTPInterceptor(session -> {
      if (session.getMethod() == Method.POST && session.getUri()
          .equalsIgnoreCase("/v1/grapher/subscription")) {
        Map<String, String> body = new HashMap<>();
        try {
          session.parseBody(body);
          Subscription sub = new Subscription(inventory, session.getRemoteIpAddress(),
              body.get("postData"));
          clientHandler.start(sub);
          Buffer buffer = new Buffer();
          sub.toJson(buffer);
          return Response.newFixedLengthResponse(Status.OK, JSON, buffer.readByteArray());
        } catch (IOException e) {
          e.printStackTrace();
        } catch (ResponseException e) {
          e.printStackTrace();
        }
      }
      return null;
    });

    addHTTPInterceptor(session -> {
      if (session.getMethod() == Method.DELETE && session.getUri()
          .equalsIgnoreCase("/v1/grapher/subscription")) {
        clientHandler.shutdown();
        Buffer buffer = new Buffer();
        return Response.newFixedLengthResponse(Status.NO_CONTENT, JSON, "");
      }
      return null;
    });

  }

  public void start() {
    try {
      start(NanoHTTPD.SOCKET_READ_TIMEOUT, true);
      showInventoryEndpoint();
    } catch (IOException e) {
      System.err.println("Couldn't start server:\n" + e);
      System.exit(-1);
    }
  }

  private Response errorResponseFor(final Exception e) {
    Buffer buffer = new Buffer();
    JsonWriter writer = JsonWriter.of(buffer);
    try {
      writer.beginObject();
      writer.name("error").value(e.getMessage());
      writer.endObject();
    } catch (IOException ignored) {
    }
    return Response.newFixedLengthResponse(Status.INTERNAL_ERROR, JSON, buffer.readByteArray());
  }

  private void showInventoryEndpoint() throws SocketException {
    System.out.println();
    Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
    for (NetworkInterface netint : Collections.list(nets)) {
      Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
      for (InetAddress inetAddress : Collections.list(inetAddresses)) {
        if (!inetAddress.isLinkLocalAddress() && inetAddress.getClass() == Inet4Address.class) {
          System.out.printf("Inventory at http://%s:%d/v1/grapher/inventory%n", inetAddress.getHostAddress(), port);
        }
      }

    }
  }
}
