package org.strykeforce.thirdcoast.talon;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import org.slf4j.Logger;

/** Utility class to check for and display TalonSRX configuration errors. */
public class Errors {

  public static void check(ErrorCode error, Logger logger) {
    if (error != null && error != ErrorCode.OK) {
      logger.error("error while configuring Talon: {}", error);
    }
  }

  public static void check(TalonSRX talon, String method, ErrorCode error, Logger logger) {
    if (error != null && error != ErrorCode.OK) {
      logger.error("Talon {}: {} error {}", talon.getDeviceID(), method, error);
    }
  }
}
