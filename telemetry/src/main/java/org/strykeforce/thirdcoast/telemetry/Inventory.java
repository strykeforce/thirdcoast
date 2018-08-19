package org.strykeforce.thirdcoast.telemetry;

import java.io.IOException;
import okio.BufferedSink;
import org.strykeforce.thirdcoast.telemetry.item.Item;

/** Represents the inventory of robot hardware and subsystems that can have telemetry streaming. */
public interface Inventory {

  /**
   * Gets an item by its inventory ID. The inventory ID is an index to an item in inventory and
   * should <b>not</b> be confused with the device ID of the underlying device the {@code Item}
   * represents.
   *
   * @param id the inventory ID to look up.
   * @return the found Item.
   */
  Item itemForId(int id);

  /**
   * Writes the grapher-format JSON inventory to the supplied sink.
   *
   * @param sink the sink to write to.
   * @throws IOException if an IO error occurs during writing.
   */
  void writeInventory(BufferedSink sink) throws IOException;

  /**
   * Writes the detailed inventory to the supplied sink. This can be used as data for web-based
   * status pages, etc.
   *
   * @param sink the sink to write to.
   * @throws IOException if an IO error occurs during writing.
   */
  void toJson(BufferedSink sink) throws IOException;
}
