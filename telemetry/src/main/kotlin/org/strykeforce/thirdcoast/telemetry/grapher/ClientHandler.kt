package org.strykeforce.thirdcoast.telemetry.grapher

import mu.KotlinLogging
import okio.Buffer
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

        val address = InetSocketAddress(subscription.client, port)
        val packet = DatagramPacket(ByteArray(0), 0, address)
        val buffer = Buffer()
        val runnable = {
            subscription.measurementsToJson(buffer)
            val bytes = buffer.readByteArray()
            packet.setData(bytes, 0, bytes.size)
            socket.send(packet)
        }

        scheduler = Executors.newSingleThreadScheduledExecutor().also {
            it.scheduleAtFixedRate(runnable, 0, 5, MILLISECONDS)
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
