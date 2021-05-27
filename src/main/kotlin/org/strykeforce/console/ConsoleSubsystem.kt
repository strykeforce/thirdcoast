package org.strykeforce.console

import edu.wpi.first.wpilibj2.command.SubsystemBase
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@ExperimentalStdlibApi
class ConsoleSubsystem @JvmOverloads constructor(var enabled: Boolean = false) : SubsystemBase() {

    val console: Console = if (enabled) ConsoleImpl() else DummyConsole()

    init {
        logger.info {
            "console display " + if (enabled) "enabled" else "disabled"
        }
    }

    override fun periodic() = console.periodic()

    fun clear() = console.clear()

    @JvmOverloads
    fun writeString(string: String, x: Int, y: Int, on: Boolean = true) =
        console.writeString(string, x, y, on)

    @JvmOverloads
    fun writeStringCentered(string: String, y: Int, on: Boolean = true) =
        console.writeStringCentered(string, y, on)

    fun getButton(switch: Console.Switch) = ConsoleButton(console, switch)

}