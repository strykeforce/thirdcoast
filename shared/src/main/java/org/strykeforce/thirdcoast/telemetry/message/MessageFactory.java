package org.strykeforce.thirdcoast.telemetry.message;

import java.net.DatagramPacket;

public class MessageFactory {

  public Message createMessage(DatagramPacket datagramPacket) {
    return GraphDataMessage.sineWaves();
  }

  public Message createRefreshMessage() {
    return new RefreshMessage();
  }

  public Message createSubscribeMessage() {
    return new SubscribeMessage();
  }

}
