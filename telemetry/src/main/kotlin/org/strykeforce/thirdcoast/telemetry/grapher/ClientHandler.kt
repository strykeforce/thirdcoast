package org.strykeforce.thirdcoast.telemetry.grapher

import mu.KotlinLogging
import okio.Buffer
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit.MILLISECONDS

private val logger = KotlinLogging.logger {}

/** Handles data streaming with Grapher client.  */
class ClientHandler(private val port: Int, private val socket: DatagramSocket) {
    private var scheduler: ScheduledExecutorService? = null

    /**
     * Start streaming the items specified in the subscription.
     *
     * @param subscription Items to stream to client
     */
    fun start(subscription: Subscription) {
        if (scheduler != null) return

        scheduler = Executors.newSingleThreadScheduledExecutor().also {
            it.scheduleAtFixedRate(
                {
                    val buffer = Buffer()
                    try {
                        subscription.measurementsToJson(buffer)
                        val bytes = buffer.readByteArray()
                        val packet = DatagramPacket(bytes, bytes.size, InetSocketAddress(subscription.client, port))
                        socket.send(packet)
                    } catch (e: IOException) {
                        logger.error("Exception sending grapher data", e)
                    }
                },
                0,
                5,
                MILLISECONDS
            )
        }
        logger.info { "sending graph data to ${subscription.client}:$port" }
    }

    /** Stop streaming to client.  */
    fun shutdown() {
        scheduler?.let { it.shutdown() }
        scheduler = null
        logger.info("stopped streaming graph data")
    }

}
