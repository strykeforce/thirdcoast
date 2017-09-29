package org.strykeforce.thirdcoast.telemetry.message;

public class GraphDataMessage extends AbstractMessage {

  private static double x = 0;

  public double[] data;

  public GraphDataMessage(double[] data) {
    super("talon");
    this.data = data;
  }

  public static GraphDataMessage sineWaves() {
    x += 0.01;

    double[] data = new double[16];
    for (int i = 0; i < 16; i++) {
      data[i] = 10 * Math.sin((i + 1) * x + i);
    }
    return new GraphDataMessage(data);
  }

}
