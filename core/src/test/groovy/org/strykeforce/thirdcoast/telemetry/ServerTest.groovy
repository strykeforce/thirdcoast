package org.strykeforce.thirdcoast.telemetry

import org.strykeforce.thirdcoast.telemetry.message.MessageFactory
import org.strykeforce.thirdcoast.telemetry.message.RefreshMessage
import org.strykeforce.thirdcoast.telemetry.message.SubscribeMessage
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

class ServerTest extends Specification {

    def "server creates client"() {

        def conditions = new PollingConditions()

        given: "a configured server"
        DatagramSocket datagramSocket = Stub(DatagramSocket)
        ClientFactory clientFactory = Mock(ClientFactory)
        MessageFactory messageFactory = Stub(MessageFactory) {
            createMessage(_ as DatagramPacket) >>> [new SubscribeMessage(),
                                                    new SubscribeMessage(),
                                                    new SubscribeMessage()]
        }
        ShutdownNotifier shutdownNotifier = Stub(ShutdownNotifier) {
            shouldShutdown() >>> [false, false, true]
        }
        Client client = Mock(Client)
        Server server = new Server(datagramSocket, clientFactory, messageFactory)
        server.setShutdownNotifier(shutdownNotifier)

        when: "a connect message arrives"
        server.run()

        then: "a client is created"
        conditions.eventually {
            2 * clientFactory.createClient() >> client
            2 * client.start()
        }
    }
}
