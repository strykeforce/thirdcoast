package org.strykeforce.thirdcoast.deadeye;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class DeadeyeMessage {

  public static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;

  public static final byte ERROR_BYTE = (byte) 0;
  public static final byte HEARTBEAT_BYTE = (byte) 1;
  public static final byte[] HEARTBEAT_BYTES = {HEARTBEAT_BYTE};
  public static final byte DATA_BYTE = (byte) 2;
  public static final byte NODATA_BYTE = (byte) 3;

  public final Type type;

  public final int latency;
  public final double[] data = new double[4];

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public DeadeyeMessage(byte[] bytes) {

    byte type = bytes.length > 0 ? bytes[0] : ERROR_BYTE;

    switch (type) {
      case HEARTBEAT_BYTE:
        this.type = Type.HEARTBEAT;
        break;
      case DATA_BYTE:
        this.type = Type.DATA;
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(BYTE_ORDER);
        buffer.position(Integer.BYTES); // skip over 1 integer
        latency = buffer.getInt();
        for (int i = 0; i < 4; i++) {
          data[i] = buffer.getDouble();
        }
        return;
      case NODATA_BYTE:
        this.type = Type.NODATA;
        break;
      default:
        this.type = Type.ERROR;
    }
    latency = 0;
  }

  @Override
  public String toString() {
    return "DeadeyeMessage{"
        + "type="
        + type
        + ", latency="
        + latency
        + ", data="
        + Arrays.toString(data)
        + '}';
  }

  public enum Type {
    HEARTBEAT,
    DATA,
    NODATA,
    ERROR
  }
}
