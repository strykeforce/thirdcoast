package org.strykeforce.healthcheck.internal

import com.sun.net.httpserver.HttpServer
import mu.KotlinLogging
import org.strykeforce.healthcheck.HealthCheckCommand
import java.io.OutputStream
import java.io.Writer
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

    private val metaCase = StringBuilder()
    private val metaTalon = StringBuilder()
    private val metaType = StringBuilder()
    private val metaOutput = StringBuilder()
    private val metaDuration = StringBuilder()

    private val timestamp = StringBuilder()
    private val talon = StringBuilder()
    private val case = StringBuilder()
    private val voltage = StringBuilder()
    private val position = StringBuilder()
    private val speed = StringBuilder()
    private val supplyCurrent = StringBuilder()
    private val statorCurrent = StringBuilder()

    private var metaIndex = 0
    private var index = 0
    override fun visit(healthCheck: RobotHealthCheck) {
        healthCheck.healthChecks.forEach { it.accept(this) }

        // remove trailing commas, grr json
        metaCase.deleteCharAt(metaCase.lastIndex)
        metaTalon.deleteCharAt(metaTalon.lastIndex)
        metaType.deleteCharAt(metaType.lastIndex)
        metaOutput.deleteCharAt(metaOutput.lastIndex)
        metaDuration.deleteCharAt(metaDuration.lastIndex)

        timestamp.deleteCharAt(timestamp.lastIndex)
        talon.deleteCharAt(talon.lastIndex)
        case.deleteCharAt(case.lastIndex)
        voltage.deleteCharAt(voltage.lastIndex)
        position.deleteCharAt(position.lastIndex)
        speed.deleteCharAt(speed.lastIndex)
        supplyCurrent.deleteCharAt(supplyCurrent.lastIndex)
        statorCurrent.deleteCharAt(statorCurrent.lastIndex)
    }

    override fun visit(healthCheck: SubsystemHealthCheck) {
        healthCheck.healthChecks.forEach { it.accept(this) }
    }

    override fun visit(healthCheck: TalonHealthCheck) {
        healthCheck.healthChecks.forEach { it.accept(this) }
    }

    override fun visit(healthCheck: TalonHealthCheckCase) {
        metaCase.append("\"${metaIndex}\":${healthCheck.case},")
        metaTalon.append("\"${metaIndex}\":${healthCheck.talon.deviceID},")
        metaType.append("\"${metaIndex}\":\"${healthCheck.type}\",")
        metaOutput.append("\"${metaIndex}\":${healthCheck.output},")
        metaDuration.append("\"${metaIndex}\":${healthCheck.duration},")
        metaIndex++

        healthCheck.data.forEach { data ->
            data.timestamp.forEachIndexed { i, v ->
                timestamp.append("\"${index + i}\":$v,")
                talon.append("\"${index + i}\":${data.deviceId},")
                case.append("\"${index + i}\":${data.case},")
                voltage.append("\"${index + i}\":${data.voltage[i]},")
                position.append("\"${index + i}\":${data.position[i]},")
                speed.append("\"${index + i}\":${data.speed[i]},")
                supplyCurrent.append("\"${index + i}\":${data.supplyCurrent[i]},")
                statorCurrent.append("\"${index + i}\":${data.statorCurrent[i]},")
            }
            index += data.timestamp.size
        }
    }

    private fun sendData(writer: Writer) {
        writer.write(
            "{" +
                    "\"case\":{$case}," +
                    "\"msec_elapsed\":{$timestamp}," +
                    "\"talon\":{$talon}," +
                    "\"voltage\":{$voltage}," +
                    "\"position\":{$position}," +
                    "\"speed\":{$speed}," +
                    "\"supply_current\":{$supplyCurrent}," +
                    "\"stator_current\":{$statorCurrent}" +
                    "}"
        )
    }

    private fun sendMeta(writer: Writer) {
        writer.write(
            "{" +
                    "\"case\":{$metaCase}," +
                    "\"talon\":{$metaTalon}," +
                    "\"type\":{$metaType}," +
                    "\"output\":{$metaOutput}," +
                    "\"duration\":{$metaDuration}" +
                    "}"
        )
    }

    fun sendHealthCheck(os: OutputStream) {
        val writer = os.writer()
        writer.write("{\"meta\":")
        sendMeta(writer)
        writer.write(",\"data\":")
        sendData(writer)
        writer.write("}")
        writer.flush()
    }
}