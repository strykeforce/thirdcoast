package org.strykeforce.thirdcoast.telemetry.grapher

import mu.KotlinLogging
import okio.Buffer
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit.MILLISECONDS

private val logger = KotlinLogging.logger {}

/** Handles data streaming with Grapher client.  */
class ClientHandler(private val port: Int, private val socket: DatagramSocket) {
  private var scheduler: ScheduledExecutorService? = null
  private var socketAddress: SocketAddress? = null

  /**
   * Start streaming the items specified in the subscription.
   *
   * @param subscription Items to stream to client
   */
  fun start(subscription: Subscription) {
    if (scheduler != null) {
      return
    }
    logger.info("Sending graph data to {}:{}", subscription.client(), port)
    socketAddress = InetSocketAddress(subscription.client(), port)
    scheduler = Executors.newSingleThreadScheduledExecutor()
    // FIXME: future not checked for exception
    scheduler!!.scheduleAtFixedRate(
      {
        val buffer = Buffer()
        try {
          subscription.measurementsToJson(buffer)
          val bytes = buffer.readByteArray()
          val packet = DatagramPacket(bytes, bytes.size, socketAddress!!)
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

  /** Stop streaming to client.  */
  fun shutdown() {
    logger.info("Stopping graph data")
    if (scheduler != null) {
      scheduler!!.shutdown()
    }
    scheduler = null
  }

}
