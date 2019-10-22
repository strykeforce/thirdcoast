package org.strykeforce.deadeye

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import edu.wpi.first.networktables.NetworkTable
import edu.wpi.first.networktables.NetworkTableInstance
import okio.Buffer

private const val ON = "On"
private const val OFF = "Off"
private const val ERROR = "Error"
private const val CONFIG = "Config"
private const val STREAM = "Stream"

internal class CameraImpl<T : TargetData>(override val id: String) : Camera<T> {

  override var targetDataListener: TargetDataListener? = null

  @Suppress("MemberVisibilityCanBePrivate")
  var targetData: TargetData = TargetData("")

  private val table: NetworkTable by lazy { NetworkTableInstance.getDefault().getTable("/Deadeye/${id[0]}/${id[1]}") }

  private val moshi: Moshi by lazy { Moshi.Builder().build() }

  override lateinit var jsonAdapter: JsonAdapter<T>

  override var enabled: Boolean
    set(value) {
      table.getEntry(if (value) ON else OFF).apply { setBoolean(true) }
    }
    get() = table.getEntry(ON).getBoolean(false)

  override val error: Boolean
    get() = table.getEntry(ERROR).getBoolean(false)

  override val config: Camera.Config
    get() = with(table.getEntry(CONFIG).getString("{}")) {
      Camera_ConfigJsonAdapter(moshi).fromJson(this) ?: throw JsonDataException("Config: $this")
    }

  override val stream: Camera.Stream
    get() = with(table.getEntry(STREAM).getString("{}")) {
      Camera_StreamJsonAdapter(moshi).fromJson(this) ?: throw JsonDataException("Stream: $this")
    }

  override fun parse(buffer: Buffer) {
    val targetData = jsonAdapter.fromJson(buffer) ?: throw JsonDataException("parse error")
    this.targetData = targetData
    targetDataListener?.onTargetData(targetData)
  }

}
