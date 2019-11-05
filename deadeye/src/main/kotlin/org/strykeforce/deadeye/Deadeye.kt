package org.strykeforce.deadeye

import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import edu.wpi.first.networktables.NetworkTable
import edu.wpi.first.networktables.NetworkTableInstance
import okio.Buffer
import java.net.DatagramPacket
import java.net.DatagramSocket
import kotlin.concurrent.thread

private const val LINK = "Config"

object Deadeye {
  private val cameraCache = mutableMapOf<String, Camera<*>>()


  private val cameraIds: List<String>
    get() = with(NetworkTableInstance.getDefault().getTable("Deadeye")) {
      subTables.flatMap { unit ->
        getSubTable(unit).subTables.map { "$unit$it" }
      }
    }

//  val cameras: Collection<Camera>
//    get() {
//      cameraIds.filterNot { cameraCache.containsKey(it) }.forEach { cameraCache[it] = CameraImpl(it) }
//      return cameraCache.values
//    }

  private val table: NetworkTable by lazy { NetworkTableInstance.getDefault().getTable("/Deadeye") }
  private val moshi: Moshi by lazy { Moshi.Builder().build() }

  var config: Config
    get() = with(table.getEntry(LINK).getString("{}")) {
      Deadeye_ConfigJsonAdapter(moshi).fromJson(this) ?: throw JsonDataException("Config: $this")
    }
    set(value) = with(table.getEntry(LINK)) {
      setString(Deadeye_ConfigJsonAdapter(moshi).toJson(value))
    }

  init {
    thread(isDaemon = true) {
      val socket = DatagramSocket(5800)
      val bytes = ByteArray(512)
      val buffer = Buffer()
      val packet = DatagramPacket(bytes, bytes.size)

      while (true) {
        socket.receive(packet)
        val id = String(packet.data, 0, 2)
        val camera = cameraCache[id] ?: throw JsonDataException("Unrecognized camera id: $id")
        buffer.write(packet.data, 2, packet.length - 2)
        camera.parse(buffer)
      }
    }
  }

  @Suppress("UNCHECKED_CAST")
  fun <T : TargetData> getCamera(id: String): Camera<T> = cameraCache.getOrPut(id) { CameraImpl<T>(id) } as Camera<T>


  @JsonClass(generateAdapter = true)
  data class Config(val address: String, val port: Int, val enabled: Boolean)

}
