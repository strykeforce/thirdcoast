package org.strykeforce.thirdcoast.telemetry

import com.squareup.moshi.JsonWriter
import mu.KotlinLogging
import okio.Buffer
import org.nanohttpd.protocols.http.NanoHTTPD
import org.nanohttpd.protocols.http.request.Method
import org.nanohttpd.protocols.http.response.Response
import org.nanohttpd.protocols.http.response.Status
import org.strykeforce.thirdcoast.telemetry.grapher.ClientHandler
import org.strykeforce.thirdcoast.telemetry.grapher.Subscription
import java.io.IOException
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

private const val JSON = "application/json"
private val logger = KotlinLogging.logger {}

/** Provides a web service to config telemetry.  */
class TelemetryController(
  inventory: Inventory,
  private val clientHandler: ClientHandler,
  private val port: Int
) : NanoHTTPD(port) {

  private val inventoryEndpoints: List<String>
    get() {
      val endpoints = ArrayList<String>(2)
      try {
        val nets = NetworkInterface.getNetworkInterfaces()
        for (netint in Collections.list(nets)) {
          val inetAddresses = netint.inetAddresses
          for (addr in Collections.list(inetAddresses)) {
            if (!addr.isLinkLocalAddress && addr.javaClass == Inet4Address::class.java) {
              endpoints.add(
                String.format("http://%s:%d/v1/grapher/inventory", addr.hostAddress, port)
              )
            }
          }
        }
      } catch (e: SocketException) {
        logger.error("Exception looking up network interfaces", e)
      }

      return endpoints
    }

  init {

    addHTTPInterceptor { session ->
      if (session.method == Method.GET && session.uri.equals(
          "/v1/grapher/inventory",
          ignoreCase = true
        )
      ) {
        val buffer = Buffer()
        try {
          inventory.writeInventory(buffer)
        } catch (e: IOException) {
          logger.error("Exception creating grapher inventory JSON", e)
          return@addHTTPInterceptor errorResponseFor(e)
        }

        logger.debug("Inventory requested from {}", session.remoteIpAddress)
        return@addHTTPInterceptor Response.newFixedLengthResponse(Status.OK, JSON, buffer.readByteArray())
      }
      null
    }

    addHTTPInterceptor { session ->
      if (session.method == Method.POST && session.uri.equals(
          "/v1/grapher/subscription",
          ignoreCase = true
        )
      ) {
        val body = HashMap<String, String>()
        try {
          session.parseBody(body)
          val sub = Subscription(inventory, session.remoteIpAddress, body["postData"]!!)
          clientHandler.start(sub)
          val buffer = Buffer()
          sub.toJson(buffer)
          return@addHTTPInterceptor Response.newFixedLengthResponse(Status.OK, JSON, buffer.readByteArray())
        } catch (t: Throwable) {
          logger.error("Exception starting grapher", t)
          return@addHTTPInterceptor errorResponseFor(t)
        }

      }
      null
    }

    addHTTPInterceptor { session ->
      if (session.method == Method.DELETE && session.uri.equals(
          "/v1/grapher/subscription",
          ignoreCase = true
        )
      ) {
        try {
          clientHandler.shutdown()
          return@addHTTPInterceptor Response.newFixedLengthResponse(Status.NO_CONTENT, JSON, "")
        } catch (t: Throwable) {
          logger.error("Exception stopping grapher", t)
          return@addHTTPInterceptor errorResponseFor(t)
        }

      }
      null
    }

    addHTTPInterceptor { session ->
      if (session.method == Method.GET && session.uri.equals("/v1/inventory", ignoreCase = true)) {
        val buffer = Buffer()
        try {
          inventory.toJson(buffer)
          return@addHTTPInterceptor Response.newFixedLengthResponse(Status.OK, JSON, buffer.readByteArray())
        } catch (t: Throwable) {
          logger.error("Exception creating detail inventory JSON", t)
          return@addHTTPInterceptor errorResponseFor(t)
        }

      }
      null
    }
  }

  override fun start() {
    try {
      start(NanoHTTPD.SOCKET_READ_TIMEOUT, true)
    } catch (e: IOException) {
      logger.error("Couldn't start server", e)
    }

    if (logger.isInfoEnabled) {
      logger.info("Started web server")
      for (end in inventoryEndpoints) {
        logger.info(end)
      }
    }
  }

  fun shutdown() {
    clientHandler.shutdown()
    super.stop()
    logger.info("Stopped web server")
  }

  private fun errorResponseFor(e: Throwable): Response {
    val buffer = Buffer()
    val writer = JsonWriter.of(buffer)
    try {
      writer.beginObject()
      writer.name("error").value(e.message)
      writer.endObject()
    } catch (ignored: IOException) {
    }

    return Response.newFixedLengthResponse(Status.INTERNAL_ERROR, JSON, buffer.readByteArray())
  }

}
