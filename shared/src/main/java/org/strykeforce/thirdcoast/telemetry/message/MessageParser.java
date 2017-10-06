package org.strykeforce.thirdcoast.telemetry.message;

import java.net.DatagramPacket;
import javax.inject.Inject;

/**
 * Parse JSON into Messages.
 */
public class MessageParser {

  @Inject
  public MessageParser() {}

  /**
   * Create a Message from a UDP datagram.
   *
   * @param datagramPacket the datagram containing JSON data.
   * @return a Message representing the datagram JSON payload.
   */
  public Message parse(DatagramPacket datagramPacket) {
    return GraphDataMessage.sineWaves();
  }

}
