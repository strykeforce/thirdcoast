package org.strykeforce.thirdcoast.telemetry

import org.strykeforce.thirdcoast.telemetry.message.MessageParser
import org.strykeforce.thirdcoast.telemetry.message.SubscribeMessage
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

import java.util.function.BooleanSupplier

class ServerTest extends Specification {

    def "server creates client"() {

        def conditions = new PollingConditions()

        given: "a configured server"
        DatagramSocket datagramSocket = Stub(DatagramSocket)
        ClientHandler clientHandler = Mock(ClientHandler)
        MessageParser messageFactory = Stub(MessageParser) {
            parse(_ as DatagramPacket) >>> [new SubscribeMessage(),
                                            new SubscribeMessage(),
                                            new SubscribeMessage()]
        }
        BooleanSupplier shutdownNotifier = Stub(BooleanSupplier) {
            getAsBoolean() >>> [false, false, true]
        }
        ClientHandler client = Mock(ClientHandler)
        Server server = new Server(datagramSocket, clientHandler, messageFactory)
        server.setShutdownNotifier(shutdownNotifier)

        when: "a connect message arrives"
        server.run()

        then: "a client is created"
        conditions.eventually {
            2 * clientHandler.start()
        }
    }
}
