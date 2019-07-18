package org.strykeforce.thirdcoast.telemetry

import okio.BufferedSink
import org.strykeforce.thirdcoast.telemetry.graphable.Graphable
import java.io.IOException

/** Represents the inventory of robot hardware and subsystems that can have telemetry streaming.  */
interface Inventory {

    /**
     * Gets an graphable by its inventory ID. The inventory ID is an index to an graphable in inventory and
     * should **not** be confused with the device ID of the underlying device the `Graphable`
     * represents.
     *
     * @param id the inventory ID to look up.
     * @return the found Graphable.
     */
    fun graphableForId(id: Int): Graphable

    /**
     * Writes the grapher-format JSON inventory to the supplied sink.
     *
     * @param sink the sink to write to.
     * @throws IOException if an IO error occurs during writing.
     */
    @Throws(IOException::class)
    fun writeInventory(sink: BufferedSink)

}
