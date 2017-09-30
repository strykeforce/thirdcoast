package org.strykeforce.thirdcoast.telemetry.message;

/**
 * A refresh message resets the robot's telemetry service and requests list of telemetry-enabled
 * components.
 */
public class RefreshMessage extends AbstractMessage {

  RefreshMessage() {
    super("refresh");
  }
}
