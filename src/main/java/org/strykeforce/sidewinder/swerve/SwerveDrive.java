package org.strykeforce.sidewinder.swerve;

import org.strykeforce.sidewinder.robot.Subsystem;

public class SwerveDrive implements Subsystem {

  private final static SwerveDrive swerveDrive = new SwerveDrive();
  private final Wheel[] wheels;

  private SwerveDrive() {
    wheels = new Wheel[]{new Wheel(0), new Wheel(1), new Wheel(2), new Wheel(3)};
  }

  public static SwerveDrive getInstance() {
    return swerveDrive;
  }

  public void set(double azimuth, double drive) {
    for (Wheel wheel : wheels) {
      wheel.set(azimuth, drive);
    }
  }

  @Override
  public void stop() {
    for (Wheel wheel : wheels) {
      wheel.stop();
    }
  }

  @Override
  public void zeroSensors() {
    int[] zeros = new int[]{2281, 3359, 2966, 1236};
    for (int i = 0; i != wheels.length; i++) {
      wheels[i].setAzimuthZero(zeros[i]);
    }
  }

}
