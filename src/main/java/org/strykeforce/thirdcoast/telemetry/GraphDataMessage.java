package org.strykeforce.thirdcoast.telemetry;

import com.jsoniter.output.JsonStream;
import java.io.OutputStream;

public class GraphDataMessage {

  public String type;
  public long timestamp;
  public double[] data;

  public GraphDataMessage(String type, double[] data) {
    this.type = type;
    this.timestamp = System.currentTimeMillis();
    this.data = data;
  }

  private static double x = 0;
  public static GraphDataMessage sineWaves() {
    x += 0.01;

    double[] data = new double[16];
    for (int i = 0; i < 16; i++) {
      data[i] = 10 * Math.sin((i + 1) * x + i);
    }
    return new GraphDataMessage("talon", data);
  }

  @Override
  public String toString() {
    return JsonStream.serialize(this);
  }

  public void serialize(OutputStream out) {
    JsonStream.serialize(this, out);
  }
}
