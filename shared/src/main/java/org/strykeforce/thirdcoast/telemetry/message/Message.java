package org.strykeforce.thirdcoast.telemetry.message;

import java.io.OutputStream;

public interface Message {

  String getType();

  void serialize(OutputStream out);

}
