package org.strykeforce.thirdcoast.telemetry.tct;

public class Main implements Runnable {

  private final MainComponent component = DaggerMainComponent.builder().build();

  public Main() { }

  @Override
  public void run() {
    Menu menu = component.menu();
    menu.display();
  }

  public static void main(String[] args) {
    Main main = new Main();
    main.run();
  }

}
