package org.strykeforce.thirdcoast.telemetry.grapher;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class ResourceHelper {

  public static String getString(String name) {
    InputStream inputStream = ResourceHelper.class.getResourceAsStream(name);
    return new BufferedReader(new InputStreamReader(inputStream))
        .lines()
        .collect(Collectors.joining("\n"));
  }
}
