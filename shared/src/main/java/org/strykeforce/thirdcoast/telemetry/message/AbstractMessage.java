package org.strykeforce.thirdcoast.telemetry.message;

import com.jsoniter.output.JsonStream;
import java.io.OutputStream;

/**
 * Abstract base class for messages.
 */
public class AbstractMessage implements Message {

  static {
    JsonStream.setIndentionStep(2);
  }

  final public String type;
  final public long timestamp;

  AbstractMessage(String type) {
    this.type = type;
    this.timestamp = System.currentTimeMillis();
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public String toString() {
    return JsonStream.serialize(this);
  }

  public void serialize(OutputStream out) {
    JsonStream.serialize(this, out);
  }

}
