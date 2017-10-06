package org.strykeforce.thirdcoast.telemetry

import spock.lang.Specification

class ClientHandlerTest extends Specification {

    def "test client construction"() {
        expect:
        SocketAddress address = new InetSocketAddress("127.0.0.1", 5555)
        def client = new ClientHandler()
    }

    /*
    DatagramClientHandler client
    PipedInputStream clientOutput
    PipedOutputStream clientInput
    ExecutorService executorService = Executors.newSingleThreadExecutor()

    def setup() {
        def socket = Mock(Socket)
        clientOutput = new PipedInputStream()
        socket.getOutputStream() >> new PipedOutputStream(clientOutput)

        clientInput = new PipedOutputStream()
        socket.getInputStream() >> new PipedInputStream(clientInput)

        client = new DatagramClientHandler(socket)
        client.start()
    }

    def cleanup() {
        client.shutdown()
    }

    def "test talon JSON"() {
        setup:
        def jsonSlurper = new JsonSlurper()

        when:
        def message = new BlockingVariable(5)
        executorService.submit {
            println "HI!"
            def buf = new byte[10]
            clientOutput.read(buf)
            println new String(buf)

//            object = jsonSlurper.parseText(scanner.readLine())
            println "HI!"
            message.set("foo")

//            message.set(jsonSlurper.parseText('{"type":"talon","data":"enable"}'))
        }

//        def lines = clientOutput.readLines("UTF-8")
//        def object = jsonSlurper.parseText(lines.first())

        then:
//        lines.size == 1
        with(message.get()) {
            type == "talon"
            timestamp instanceof Long
            data instanceof List
            data.size == 16
        }
    }

    class Command {
        String type = "command"
        String data
    }

    def "json"() {
        expect:
        def json = JsonOutput.toJson(new Command(data: "enable"))
        assert json == '{"type":"command","data":"enable"}'
    }
*/
}