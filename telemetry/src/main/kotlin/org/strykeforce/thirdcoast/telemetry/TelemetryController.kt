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
            val nets = NetworkInterface.getNetworkInterfaces()
            for (netint in Collections.list(nets)) {
                val inetAddresses = netint.inetAddresses
                for (addr in Collections.list(inetAddresses)) {
                    if (!addr.isLinkLocalAddress && addr.javaClass == Inet4Address::class.java)
                        endpoints += "http://${addr.hostAddress}:$port/v1/grapher/inventory"
                }
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
                inventory.writeInventory(buffer)

                logger.debug { "inventory requested from ${session.remoteIpAddress}" }
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
                    logger.error("couldn't start grapher", t)
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
                    logger.error("couldn't stop grapher", t)
                    return@addHTTPInterceptor errorResponseFor(t)
                }

            }
            null
        }
    }

    /** Start web service to listen for HTTP commands that control telemetry service. */
    override fun start() {
        try {
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, true)
        } catch (e: IOException) {
            logger.error("couldn't start web service", e)
        }

        if (logger.isInfoEnabled) {
            logger.info("started web service")
            for (end in inventoryEndpoints) {
                logger.info(end)
            }
        }
    }

    /** Stop streaming to client and shut down web service. */
    fun shutdown() {
        clientHandler.shutdown()
        super.stop()
        logger.info("stopped web service")
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
