package org.strykeforce.thirdcoast.telemetry.tct.servo;

import edu.wpi.first.wpilibj.Servo;
import javax.inject.Inject;
import org.strykeforce.thirdcoast.telemetry.tct.di.ModeScoped;

@ModeScoped
public class ServoSet {

  private Servo servo;

  @Inject
  public ServoSet() {
  }

  public Servo getServo() {
    return servo;
  }

  public void setServo(Servo servo) {
    this.servo = servo;
  }
}
