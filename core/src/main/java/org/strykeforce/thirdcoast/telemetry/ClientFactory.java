package org.strykeforce.thirdcoast.telemetry;

import java.net.DatagramSocket;

class ClientFactory {

  Client createClient() {
    return new Client();
  }

}
