package org.strykeforce.thirdcoast.telemetry.message;

/**
 * A subscribe message registers a list of desired components and measurements.
 */

public class SubscribeMessage extends AbstractMessage {

  SubscribeMessage() {
    super("subscribe");
  }
}
