package org.strykeforce.thirdcoast.telemetry.message;

import java.io.OutputStream;

/**
 * Represents a JSON telemetry message.
 */
public interface Message {

  /**
   * Returns a string representing of the message type.
   *
   * @return a string representing the message type.
   */
  String getType();

  /**
   * Write the JSON representation of this message to the supplied OutputStream.
   *
   * @param out write to this OutputStream.
   */
  void serialize(OutputStream out);

}
