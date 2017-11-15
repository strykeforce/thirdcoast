package org.strykeforce.thirdcoast.telemetry.tct

import org.jline.reader.LineReader

import spock.lang.Specification

import javax.inject.Provider

class CommandAdapterTest extends Specification {

    def "sorts by weight"() {
        given:
        def servoMenu = Stub(Menu)
        def dioMenu = Stub(Menu)
        def talonMenu = Stub(Menu)
        def reader = Stub(LineReader)

        def commands = new HashSet<Command>()
        commands.add(new TalonModeCommand(talonMenu, reader))
        commands.add(new ServoModeCommand(servoMenu, reader))
        commands.add(new DioModeCommand(dioMenu, reader))
        commands.add(new QuitCommand(reader))

        when:
        def adapter = new CommandAdapter("MAIN", commands)

        then:
        adapter.commands.get(0) instanceof TalonModeCommand
        adapter.commands.get(1) instanceof ServoModeCommand
        adapter.commands.get(2) instanceof DioModeCommand
        adapter.commands.get(3) instanceof QuitCommand
    }
}
