package org.strykeforce.thirdcoast.telemetry.tct

import org.jline.reader.LineReader
import org.strykeforce.thirdcoast.telemetry.tct.dio.DioMenuComponent
import org.strykeforce.thirdcoast.telemetry.tct.servo.ServoMenuComponent
import org.strykeforce.thirdcoast.telemetry.tct.talon.di.TalonMenuComponent
import spock.lang.Specification

import javax.inject.Provider

class CommandAdapterTest extends Specification {

    def "sorts by weight"() {
        given:
        Provider<TalonMenuComponent.Builder> talon = Stub()
        Provider<ServoMenuComponent.Builder> servo = Stub()
        Provider<DioMenuComponent.Builder> dio = Stub()
        def reader = Stub(LineReader)

        def commands = new HashSet<Command>()
        commands.add(new TalonModeCommand(talon, reader))
        commands.add(new ServoModeCommand(servo, reader))
        commands.add(new DioModeCommand(dio, reader))
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
