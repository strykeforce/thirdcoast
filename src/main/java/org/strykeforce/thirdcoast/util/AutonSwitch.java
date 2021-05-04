package org.strykeforce.thirdcoast.util;

import edu.wpi.first.wpilibj.DigitalInput;
import java.util.ArrayList;
import java.util.List;

public class AutonSwitch {

  private final List<DigitalInput> digitalInputs;

  /**
   * Construct with the specified digital inputs.
   *
   * @param digitalInputs - in order from least to most significant bits.
   */
  public AutonSwitch(List<DigitalInput> digitalInputs) {
    this.digitalInputs = digitalInputs;
  }

  /**
   * Construct with digital inputs starting with DIO channel 0
   *
   * @param bits - number of digital inputs
   */
  public AutonSwitch(int bits) {
    List<DigitalInput> digitalInputs = new ArrayList<>(bits);
    for (int i = 0; i < bits; i++) {
      digitalInputs.add(new DigitalInput(i));
    }
    this.digitalInputs = digitalInputs;
  }

  /**
   * Read switch value.
   *
   * @return numeric value of switch
   */
  public int position() {
    int val = 0;
    for (int i = digitalInputs.size(); i-- > 0; ) {
      val = val << 1;
      val = (val & 0xFE) | (digitalInputs.get(i).get() ? 0 : 1);
    }
    return val;
  }
}
