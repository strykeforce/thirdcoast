package org.strykeforce.thirdcoast.telemetry;

import java.io.IOException;
import okio.BufferedSink;
import org.strykeforce.thirdcoast.telemetry.item.Item;

/**
 * Represents the inventory of robot hardware and subsystems that can have telemetry streaming.
 */
public interface Inventory {

  Item itemForId(int id);

  void writeInventory(BufferedSink sink) throws IOException;

  void toJson(BufferedSink sink) throws IOException;

}
