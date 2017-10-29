package org.strykeforce.thirdcoast.telemetry.tct;

import edu.wpi.first.wpilibj.DriverStation;
import javax.inject.Inject;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

public class Prompts {

  private final static String ENABLED = new AttributedStringBuilder()
      .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN)).append("[enabled]")
      .toAnsi();
  private final static String DISABLED = new AttributedStringBuilder()
      .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.RED)).append("[disabled]")
      .toAnsi();

  @Inject
  public Prompts() {}


  String rightPrompt() {
    boolean enabled = DriverStation.getInstance().isEnabled();
    return enabled ? ENABLED : DISABLED;
  }


}
