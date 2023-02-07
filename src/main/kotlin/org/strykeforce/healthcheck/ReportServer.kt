package org.strykeforce.healthcheck

import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import mu.KotlinLogging
import java.net.InetSocketAddress

private val logger = KotlinLogging.logger {}

class ReportServer {
    private val httpServer = HttpServer.create(InetSocketAddress(2767), 0).apply {
        createContext("/run", HttpHandler {
            checkNotNull(it) { "SubscriptionHandler handle called with null HttpExchange" }
            logger.info { "${it.requestMethod} ${it.requestURI}" }
            HealthCheckCommand.BUTTON.setPressed(true)

            it.responseHeaders.let { headers ->
                headers["Content-Type"] = "text/plain; charset=utf-8"
            }

            it.sendResponseHeaders(200, 0)

            it.responseBody.writer().use { out ->
                out.write("HELLO")
            }
        })

        start()
    }


}