package org.strykeforce.healthcheck.internal

import com.sun.net.httpserver.HttpServer
import mu.KotlinLogging
import org.strykeforce.healthcheck.HealthCheckCommand
import java.io.OutputStream
import java.net.InetSocketAddress

private val logger = KotlinLogging.logger {}

class ReportServer(private val healthCheck: RobotHealthCheck) {
    private val httpServer = HttpServer.create(InetSocketAddress(2767), 0).apply {
        createContext("/run") {
            logger.info { "${it.requestMethod} ${it.requestURI}" }
            HealthCheckCommand.BUTTON.setPressed(true)

            it.responseHeaders.let { headers ->
                headers["Content-Type"] = "text/plain; charset=utf-8"
            }

            it.sendResponseHeaders(200, 0)

            it.responseBody.writer().use { out ->
                out.write("BUTTON pressed")
            }
        }

        createContext("/data") { httpExchange ->
            val jsonVisitor = JsonVisitor()
            jsonVisitor.visit(healthCheck)

            httpExchange.responseHeaders.let { headers ->
                headers["Content-Type"] = "application/json"
            }
            httpExchange.sendResponseHeaders(200, 0)
            httpExchange.responseBody.use { jsonVisitor.sendHealthCheck(it) }
        }

        start()
    }


}

class JsonVisitor : HealthCheckVisitor {

    private val meta = mapOf(
        "case" to StringBuilder(),
        "case_uuid" to StringBuilder(),
        "name" to StringBuilder(),
        "talon" to StringBuilder(),
        "type" to StringBuilder(),
        "output" to StringBuilder(),
        "duration" to StringBuilder(),
    )

    private val data = mutableMapOf(
        "msec_elapsed" to StringBuilder(),
        "talon" to StringBuilder(),
        "case" to StringBuilder(),
        "voltage" to StringBuilder(),
        "position" to StringBuilder(),
        "speed" to StringBuilder(),
        "supply_current" to StringBuilder(),
        "stator_current" to StringBuilder(),
    )

    private var name = ""

    private var metaIndex = 0
    private var index = 0
    override fun visit(healthCheck: RobotHealthCheck) {
        healthCheck.healthChecks.forEach { it.accept(this) }

        // remove trailing commas, grr json
        meta.values.forEach { it.deleteCharAt(it.lastIndex) }
        data.values.forEach { it.deleteCharAt(it.lastIndex) }
    }

    override fun visit(healthCheck: SubsystemHealthCheck) {
        name = healthCheck.name
        healthCheck.healthChecks.forEach { it.accept(this) }
    }

    override fun visit(healthCheck: TalonHealthCheck) {
        healthCheck.healthChecks.forEach { it.accept(this) }
    }

    override fun visit(healthCheck: TalonHealthCheckCase) {
        meta.getValue("case").append("\"${metaIndex}\":${healthCheck.case},")
        meta.getValue("case_uuid").append("\"${metaIndex}\":\"${healthCheck.uuid}\",")
        meta.getValue("name").append("\"${metaIndex}\":\"$name\",")
        meta.getValue("talon").append("\"${metaIndex}\":${healthCheck.talon.deviceID},")
        meta.getValue("type").append("\"${metaIndex}\":\"${healthCheck.type}\",")
        meta.getValue("output").append("\"${metaIndex}\":${healthCheck.output},")
        meta.getValue("duration").append("\"${metaIndex}\":${healthCheck.duration},")
        metaIndex++

        healthCheck.data.forEach { healthCheckData ->
            healthCheckData.timestamp.forEachIndexed { i, v ->
                data.getValue("msec_elapsed").append("\"${index + i}\":$v,")
                data.getValue("talon").append("\"${index + i}\":${healthCheckData.deviceId},")
                data.getValue("case").append("\"${index + i}\":${healthCheckData.case},")
                data.getValue("voltage").append("\"${index + i}\":${healthCheckData.voltage[i]},")
                data.getValue("position").append("\"${index + i}\":${healthCheckData.position[i]},")
                data.getValue("speed").append("\"${index + i}\":${healthCheckData.speed[i]},")
                data.getValue("supply_current").append("\"${index + i}\":${healthCheckData.supplyCurrent[i]},")
                data.getValue("stator_current").append("\"${index + i}\":${healthCheckData.statorCurrent[i]},")
            }
            index += healthCheckData.timestamp.size
        }
    }

    fun sendHealthCheck(os: OutputStream) {
        val writer = os.writer()

        writer.write("{\"meta\":{")
        for ((i, key) in meta.keys.withIndex()) {
            writer.write("\"$key\":{${meta[key]}}")
            if (i < meta.size - 1) writer.write(",")
        }

        writer.write("},\"data\":{")
        for ((i, key) in data.keys.withIndex()) {
            writer.write("\"$key\":{${data[key]}}")
            if (i < data.size - 1) writer.write(",")
        }

        writer.write("}}")
        writer.flush()
    }
}