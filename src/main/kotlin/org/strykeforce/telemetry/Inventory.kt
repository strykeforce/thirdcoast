package org.strykeforce.telemetry

import okio.BufferedSink
import org.strykeforce.telemetry.item.Measurable
import java.io.IOException

/** Represents the inventory of robot hardware and subsystems that can have telemetry streaming.  */
interface Inventory {

  /**
   * Gets a measurable item by its inventory ID. The inventory ID is an index to a measurable item in inventory and
   * should **not** be confused with the device ID of the underlying device the `Measurable` represents.
   *
   * @param id the inventory ID to look up.
   * @return the found Measurable item.
   */
  fun itemForId(id: Int): Measurable

  /**
   * Writes the grapher-format JSON inventory to the supplied sink.
   *
   * @param sink the sink to write to.
   * @throws IOException if an IO error occurs during writing.
   */
  @Throws(IOException::class)
  fun writeInventory(sink: BufferedSink)

}
