package org.strykeforce.thirdcoast.telemetry.grapher;

import java.io.IOException;
import okio.BufferedSink;

public interface Inventory {

  Item itemForId(int id);

  void writeInventory(BufferedSink sink) throws IOException;

}
