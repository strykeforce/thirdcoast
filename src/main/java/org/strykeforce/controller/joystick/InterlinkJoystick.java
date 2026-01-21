package org.strykeforce.controller.joystick;

import edu.wpi.first.wpilibj.Joystick;

public class InterlinkJoystick implements ControllerInterface {
  Joystick interlink;

  public InterlinkJoystick(Joystick Joystick) {
    interlink = Joystick;
  }

  @Override
  public double getFwd() {
    return interlink.getRawAxis(Axis.LEFT_X.id);
  }

  @Override
  public double getStr() {
    return interlink.getRawAxis(Axis.LEFT_Y.id);
  }

  @Override
  public double getYaw() {
    return interlink.getRawAxis(Axis.RIGHT_Y.id);
  }

  // Interlink Controller Mapping
  public enum Axis {
    RIGHT_X(1),
    RIGHT_Y(0),
    LEFT_X(2),
    LEFT_Y(5),
    TUNER(6),
    LEFT_BACK(4),
    RIGHT_BACK(3);

    public final int id;

    Axis(int id) {
      this.id = id;
    }
  }

  public enum Shoulder {
    RIGHT_DOWN(2),
    LEFT_DOWN(4),
    LEFT_UP(5);

    public final int id;

    Shoulder(int id) {
      this.id = id;
    }
  }

  public enum Toggle {
    LEFT_TOGGLE(1);

    public final int id;

    Toggle(int id) {
      this.id = id;
    }
  }

  public enum InterlinkButton {
    RESET(3),
    HAMBURGER(14),
    X(15),
    UP(16),
    DOWN(17);

    public final int id;

    InterlinkButton(int id) {
      this.id = id;
    }
  }

  public enum Trim {
    LEFT_Y_POS(7),
    LEFT_Y_NEG(6),
    LEFT_X_POS(8),
    LEFT_X_NEG(9),
    RIGHT_X_POS(10),
    RIGHT_X_NEG(11),
    RIGHT_Y_POS(12),
    RIGHT_Y_NEG(13);

    public final int id;

    Trim(int id) {
      this.id = id;
    }
  }
}
