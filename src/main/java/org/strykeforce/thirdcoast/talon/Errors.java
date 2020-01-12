package org.strykeforce.thirdcoast.talon;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import org.slf4j.Logger;

/** Utility class to check for and display TalonSRX configuration errors. */
public class Errors {

  private static boolean summarized = true;
  private static int count;

  public static void check(ErrorCode error, Logger logger) {
    if (error != null && error != ErrorCode.OK) {
      if (summarized) count++;
      else logger.error("error while configuring Talon: {}", error);
    }
  }

  public static void check(TalonSRX talon, String method, ErrorCode error, Logger logger) {
    if (error != null && error != ErrorCode.OK) {
      if (summarized) count++;
      else logger.error("Talon {}: {} error {}", talon.getDeviceID(), method, error);
    }
  }

  public static boolean isSummarized() {
    return summarized;
  }

  public static void setSummarized(boolean summarized) {
    Errors.summarized = summarized;
  }

  public static int getCount() {
    return count;
  }

  public static void setCount(int count) {
    Errors.count = count;
  }
}
