package org.strykeforce.thirdcoast.telemetry.message;

import java.net.DatagramPacket;

/**
 * Factory that creates Message objects.
 */
public class MessageFactory {

  /**
   * Create a Message from a UDP datagram.
   *
   * @param datagramPacket the datagram containing JSON data.
   * @return a Message representing the datagram JSON payload.
   */
  public Message createMessage(DatagramPacket datagramPacket) {
    return GraphDataMessage.sineWaves();
  }

  /**
   * Create a {@link RefreshMessage}.
   * @return a new RefreshMessage.
   */
  public Message createRefreshMessage() {
    return new RefreshMessage();
  }

  /**
   * Create a {@link SubscribeMessage}.
   * @return a new SubscribeMessage.
   */
  public Message createSubscribeMessage() {
    return new SubscribeMessage();
  }

}
