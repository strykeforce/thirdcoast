package org.strykeforce.thirdcoast.talon;

import com.ctre.phoenix.ErrorCode;
import org.slf4j.Logger;

class Errors {

  static void check(ErrorCode error, Logger logger) {
    if (error != null && error != ErrorCode.OK) {
      logger.error("error while configuring Talon: {}", error);
    }
  }
}
