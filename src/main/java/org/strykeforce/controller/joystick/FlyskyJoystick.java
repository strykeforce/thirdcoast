package org.strykeforce.controller.joystick;

import edu.wpi.first.wpilibj.Joystick;

public class FlyskyJoystick implements ControllerInterface {
  private Joystick flysky;

  public FlyskyJoystick(Joystick joystick) {
    flysky = joystick;
  }

  @Override
  public double getFwd() {
    return -flysky.getRawAxis(Axis.FWD.id);
  }

  @Override
  public double getStr() {
    return flysky.getRawAxis(Axis.STR.id);
  }

  @Override
  public double getYaw() {
    return flysky.getRawAxis(Axis.YAW.id);
  }

  public enum Axis {
    FWD(0),
    STR(1),
    YAW(2);

    public final int id;

    Axis(int id) {
      this.id = id;
    }
  }

  public enum Button {
    SWA(1),
    SWB_UP(2),
    SWB_DWN(3),
    M_SWC(4),
    SWD(5),
    M_SWE(6),
    SWF_UP(7),
    SWF_DWN(8),
    SWG_UP(9),
    SWG_DWN(10),
    M_SWH(11),
    M_LTRIM_UP(12),
    M_LTRIM_DWN(13),
    M_LTRIM_L(14),
    M_LTRIM_R(15),
    M_RTRIM_UP(16),
    M_RTRIM_DWN(17),
    M_RTRIM_L(18),
    M_RTRIM_R(19);

    public final int id;

    Button(int id) {
      this.id = id;
    }
  }
}
