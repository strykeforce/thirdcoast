package org.strykeforce.thirdcoast.deadeye;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class DeadeyeMessage {

  public static final int TYPE_FRAME_DATA = 0xDEADDA7A;
  public static final int TYPE_PING = 0xDEADBACC;
  public static final int TYPE_PONG = 0xDEADCCAB;

  public final int type;
  public final double[] data = new double[4];
  public final int latency;

  DeadeyeMessage(byte[] bytes) {
    ByteBuffer buffer = ByteBuffer.wrap(bytes);
    buffer.order(DeadeyeService.BYTE_ORDER);
    buffer.rewind();
    type = buffer.getInt();

    switch (type) {
      case TYPE_FRAME_DATA:
        latency = buffer.getInt();
        for (int i = 0; i < 4; i++) {
          data[i] = buffer.getDouble();
        }
        //        System.out.println("FRAME DATA:" + Arrays.toString(data));
        return;

      case TYPE_PONG:
        //        System.out.println("PONG DATA");
        break;

      default:
        System.out.println("UNKNOWN TYPE: " + type);
    }
    latency = 0;
  }

  @Override
  public String toString() {
    return "VisionData{"
        + "type="
        + String.format("0x%08X", type)
        + ", data="
        + Arrays.toString(data)
        + ", latency="
        + latency
        + '}';
  }
}
