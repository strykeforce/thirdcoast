package org.strykeforce.thirdcoast.telemetry

import okio.BufferedSink
import org.strykeforce.thirdcoast.telemetry.item.Item
import java.io.IOException

/** Represents the inventory of robot hardware and subsystems that can have telemetry streaming.  */
interface Inventory {

  /**
   * Gets an item by its inventory ID. The inventory ID is an index to an item in inventory and
   * should **not** be confused with the device ID of the underlying device the `Item`
   * represents.
   *
   * @param id the inventory ID to look up.
   * @return the found Item.
   */
  fun itemForId(id: Int): Item

  /**
   * Writes the grapher-format JSON inventory to the supplied sink.
   *
   * @param sink the sink to write to.
   * @throws IOException if an IO error occurs during writing.
   */
  @Throws(IOException::class)
  fun writeInventory(sink: BufferedSink)

  /**
   * Writes the detailed inventory to the supplied sink. This can be used as data for web-based
   * status pages, etc.
   *
   * @param sink the sink to write to.
   * @throws IOException if an IO error occurs during writing.
   */
  @Throws(IOException::class)
  fun toJson(sink: BufferedSink)
}
