package org.strykeforce.thirdcoast.telemetry

import mu.KotlinLogging
import okio.Buffer
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.AbstractHandler
import org.eclipse.jetty.server.handler.DefaultHandler
import org.eclipse.jetty.server.handler.HandlerList
import org.strykeforce.thirdcoast.telemetry.grapher.ClientHandler
import org.strykeforce.thirdcoast.telemetry.grapher.Subscription
import java.net.Inet4Address
import java.net.NetworkInterface
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


private const val JSON = "application/json"
private const val GRAPHER = "/v1/grapher"
private const val INVENTORY = "$GRAPHER/inventory"
private const val SUBSCRIPTION = "$GRAPHER/subscription"

private val logger = KotlinLogging.logger {}

internal class TelemetryControllerHandler(private val inventory: Inventory, private val clientHandler: ClientHandler) :
    AbstractHandler() {

    override fun handle(
        target: String,
        baseRequest: Request,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        logger.debug { "${request.method} $target" }

        baseRequest.isHandled = true

        if (target.toLowerCase() == INVENTORY && request.method == "GET") {
            val buffer = Buffer()
            inventory.writeInventory(buffer)
            response.writeJson(buffer)
            logger.info { "inventory requested from ${request.remoteAddr}" }
            return
        }

        if (target.toLowerCase() == SUBSCRIPTION) {
            if (request.method == "POST") {
                val sub = Subscription(inventory, request.remoteAddr, request.reader.readText())
                clientHandler.start(sub)
                val buffer = Buffer()
                sub.toJson(buffer)
                response.writeJson(buffer)
                logger.info { "subscription started from ${request.remoteAddr}" }
                return
            }

            if (request.method == "DELETE") {
                clientHandler.shutdown()
                logger.info { "subscription stopped from ${request.remoteAddr}" }
                return
            }
        }
        baseRequest.isHandled = false
    }
}

private fun HttpServletResponse.writeJson(buffer: Buffer) {
    this.contentType = JSON
    this.status = HttpServletResponse.SC_OK
    this.writer.print(buffer.readUtf8())
}

/** Provides a web service to config telemetry.  */
class TelemetryController(
    inventory: Inventory,
    private val clientHandler: ClientHandler,
    private val port: Int
) {


    private val server = Server(port).apply {
        handler = HandlerList().apply {
            handlers = arrayOf(TelemetryControllerHandler(inventory, clientHandler), DefaultHandler())
        }
    }

    private val inventoryEndpoints: List<String>
        get() {
            val endpoints = mutableListOf<String>()
            NetworkInterface.getNetworkInterfaces().iterator().forEach { ni ->
                ni.inetAddresses.iterator().forEach { addr ->
                    if (addr is Inet4Address && !addr.isLinkLocalAddress)
                        endpoints += "http://${addr.hostAddress}:$port$INVENTORY"
                }
            }
            return endpoints
        }

    /** Start web service to listen for HTTP commands that control telemetry service. */
    fun start() {
        server.start()
        logger.info("started web service")
        inventoryEndpoints.forEach(logger::info)
    }

    /** Stop streaming to client and shut down web service. */
    fun shutdown() {
        clientHandler.shutdown()
        server.stop()
        logger.info("stopped web service")
    }
}
