package org.strykeforce.thirdcoast.telemetry;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;
import com.squareup.moshi.JsonWriter;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import javax.inject.Named;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.request.Method;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.grapher.ClientHandler;
import org.strykeforce.thirdcoast.telemetry.grapher.Subscription;

/** Provides a web service to control telemetry. */
@ParametersAreNonnullByDefault
@AutoFactory
public class TelemetryController extends NanoHTTPD {

  static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final String JSON = "application/json";

  private final ClientHandler clientHandler;
  private final int port;

  @Inject
  public TelemetryController(
      final Inventory inventory,
      @Provided final ClientHandler clientHandler,
      @Provided final @Named("server") int port) {
    super(port);
    this.clientHandler = clientHandler;
    this.port = port;

    addHTTPInterceptor(
        session -> {
          if (session.getMethod() == Method.GET
              && session.getUri().equalsIgnoreCase("/v1/grapher/inventory")) {
            Buffer buffer = new Buffer();
            try {
              inventory.writeInventory(buffer);
            } catch (IOException e) {
              logger.error("Exception creating grapher inventory JSON", e);
              return errorResponseFor(e);
            }
            logger.debug("Inventory requested from {}", session.getRemoteIpAddress());
            return Response.newFixedLengthResponse(Status.OK, JSON, buffer.readByteArray());
          }
          return null;
        });

    addHTTPInterceptor(
        session -> {
          if (session.getMethod() == Method.POST
              && session.getUri().equalsIgnoreCase("/v1/grapher/subscription")) {
            Map<String, String> body = new HashMap<>();
            try {
              session.parseBody(body);
              Subscription sub =
                  new Subscription(inventory, session.getRemoteIpAddress(), body.get("postData"));
              clientHandler.start(sub);
              Buffer buffer = new Buffer();
              sub.toJson(buffer);
              return Response.newFixedLengthResponse(Status.OK, JSON, buffer.readByteArray());
            } catch (Throwable t) {
              logger.error("Exception starting grapher", t);
              return errorResponseFor(t);
            }
          }
          return null;
        });

    addHTTPInterceptor(
        session -> {
          if (session.getMethod() == Method.DELETE
              && session.getUri().equalsIgnoreCase("/v1/grapher/subscription")) {
            try {
              clientHandler.shutdown();
              Buffer buffer = new Buffer();
              return Response.newFixedLengthResponse(Status.NO_CONTENT, JSON, "");
            } catch (Throwable t) {
              logger.error("Exception stopping grapher", t);
              return errorResponseFor(t);
            }
          }
          return null;
        });

    addHTTPInterceptor(
        session -> {
          if (session.getMethod() == Method.GET
              && session.getUri().equalsIgnoreCase("/v1/inventory")) {
            Buffer buffer = new Buffer();
            try {
              inventory.toJson(buffer);
              return Response.newFixedLengthResponse(Status.OK, JSON, buffer.readByteArray());
            } catch (Throwable t) {
              logger.error("Exception creating detail inventory JSON", t);
              return errorResponseFor(t);
            }
          }
          return null;
        });
  }

  @Override
  public void start() {
    try {
      start(NanoHTTPD.SOCKET_READ_TIMEOUT, true);
    } catch (IOException e) {
      logger.error("Couldn't start server", e);
    }
    if (logger.isInfoEnabled()) {
      logger.info("Started web server");
      for (String end : getInventoryEndpoints()) {
        logger.info(end);
      }
    }
  }

  public void shutdown() {
    clientHandler.shutdown();
    super.stop();
    logger.info("Stopped web server");
  }

  @NotNull
  private Response errorResponseFor(final Throwable e) {
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

  public List<String> getInventoryEndpoints() {
    List<String> endpoints = new ArrayList<>(2);
    try {
      Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
      for (NetworkInterface netint : Collections.list(nets)) {
        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        for (InetAddress addr : Collections.list(inetAddresses)) {
          if (!addr.isLinkLocalAddress() && addr.getClass() == Inet4Address.class) {
            endpoints.add(
                String.format("http://%s:%d/v1/grapher/inventory", addr.getHostAddress(), port));
          }
        }
      }
    } catch (SocketException e) {
      logger.error("Exception looking up network interfaces", e);
    }
    return endpoints;
  }
}
