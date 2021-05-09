package org.strykeforce.telemetry

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import mu.KotlinLogging
import okio.Buffer
import org.strykeforce.telemetry.grapher.ClientHandler
import org.strykeforce.telemetry.grapher.Subscription
import java.io.OutputStream
import java.net.DatagramSocket
import java.net.Inet4Address
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.util.concurrent.Executors

private const val SERVER_PORT = 5800
private const val CLIENT_PORT = 5801
private const val GRAPHER = "/v1/grapher"
private const val INVENTORY_ENDPOINT = "$GRAPHER/inventory"
private const val SUBSCRIPTION_ENDPOINT = "$GRAPHER/subscription"

private val logger = KotlinLogging.logger {}

private class InventoryHandler(
    private val inventory: Inventory,
) : HttpHandler {
    override fun handle(exchange: HttpExchange?) {
        checkNotNull(exchange) { "InventoryHandler handle called with null HttpExchange" }
        if (exchange.requestMethod.toUpperCase() != "GET") error("InventoryHandler expects GET request method")

        val buffer = Buffer()
        inventory.writeInventory(buffer)
        exchange.jsonResponse(buffer)
        buffer.close()
        logger.info { "inventory requested from ${exchange.remoteAddress}" }
    }
}

private class SubscriptionHandler(
    private val inventory: Inventory,
    private val clientHandler: ClientHandler
) : HttpHandler {
    override fun handle(exchange: HttpExchange?) {
        checkNotNull(exchange) { "SubscriptionHandler handle called with null HttpExchange" }
        logger.debug { "${exchange.requestMethod} $SUBSCRIPTION_ENDPOINT" }
        if (exchange.requestMethod.toUpperCase() == "POST") {
            val buffer = Buffer()
            buffer.readFrom(exchange.requestBody)
            val sub = Subscription(inventory, exchange.remoteAddress.address, buffer.readUtf8())
            clientHandler.start(sub)
            buffer.clear()
            sub.toJson(buffer)
            exchange.jsonResponse(buffer)
            buffer.close()
            logger.info { "subscription started from ${exchange.remoteAddress}" }
            return
        }

        if (exchange.requestMethod.toUpperCase() == "DELETE") {
            clientHandler.shutdown()
            exchange.sendResponseHeaders(204, -1)
            logger.info { "subscription stopped from ${exchange.remoteAddress}" }
            return
        }

        error("SubscriptionHandler expects POST or DELETE request method")
    }

}

private fun HttpExchange.jsonResponse(buffer: Buffer) {
    this.responseHeaders.let {
        it["Content-Type"] = "application/json; charset=utf-8"
    }
    this.sendResponseHeaders(200, buffer.size)
    val out: OutputStream = this.responseBody
    buffer.writeTo(out)
    out.flush()
    out.close()
}


/** Provides a web service to config telemetry.  */
class TelemetryController(
    private val inventory: Inventory,
    private val clientHandler: ClientHandler,
    private val socket: InetSocketAddress
) {

    constructor(inventory: Inventory) : this(
        inventory,
        ClientHandler(CLIENT_PORT, DatagramSocket()),
        InetSocketAddress(SERVER_PORT)
    )


    /** HTTP server. */
    private var server: HttpServer? = null

    /** Start web service to listen for HTTP commands that control telemetry service. */
    fun start() {
        check(server == null) { "start called while already started" }
        server = HttpServer.create().apply {
            bind(socket, 0)
            executor = Executors.newSingleThreadExecutor()
            createContext(INVENTORY_ENDPOINT, InventoryHandler(inventory))
            createContext(SUBSCRIPTION_ENDPOINT, SubscriptionHandler(inventory, clientHandler))
        }
        server?.start()
        logger.info("started web service")
        inventoryEndpoints.forEach(logger::info)
    }

    /** Stop streaming to client and shut down web service. */
    fun shutdown() {
        clientHandler.shutdown()
        server?.stop(0)
        server = null
        logger.info("stopped web service")
    }

    private val inventoryEndpoints: List<String>
        get() {
            val endpoints = mutableListOf<String>()
            NetworkInterface.getNetworkInterfaces().iterator().forEach { ni ->
                ni.inetAddresses.iterator().forEach { addr ->
                    if (addr is Inet4Address && !addr.isLinkLocalAddress)
                        endpoints += "http://${addr.hostAddress}:${socket.port}$INVENTORY_ENDPOINT"
                }
            }
            return endpoints
        }

}
