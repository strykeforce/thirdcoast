package org.strykeforce.thirdcoast.telemetry.tct;

import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

public class Messages {

  public final static String NO_TALONS = boldRed("\n*** no talons selected yet ***\n");

  public static String menuHelp(int size) {
    String msg = String.format("please enter a number between 1-%d or <enter> to return", size);
    return boldRed(msg);
  }

  public static String prompt(String prompt) {
    return boldYellow(prompt);
  }

  public static String bold(String message) {
    return new AttributedStringBuilder().style(AttributedStyle.BOLD)
        .append(message)
        .toAnsi();
  }

  public static String boldRed(String message) {
    return boldWithForeground(message, AttributedStyle.RED);
  }

  public static String boldGreen(String message) {
    return boldWithForeground(message, AttributedStyle.GREEN);
  }

  public static String boldYellow(String message) {
    return boldWithForeground(message, AttributedStyle.YELLOW);
  }

  public static String boldWithForeground(String message, int style) {
    return new AttributedStringBuilder()
        .style(AttributedStyle.BOLD.foreground(style))
        .append(message)
        .toAnsi();
  }


}
