package org.strykeforce.thirdcoast.deadeye;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Debug {
  private static void debugDatagramPacket(DatagramPacket p) {
    byte[] b = Arrays.copyOf(p.getData(), p.getLength());
    debugByteArray(b);
  }

  private static void debugByteBuffer(ByteBuffer b) {
    b.rewind();
    byte[] bytes = new byte[b.remaining()];
    b.get(bytes);
    debugByteArray(bytes);
  }

  private static void debugByteArray(byte[] b) {
    System.out.println("Bytes = " + Arrays.toString(b));
  }
}
