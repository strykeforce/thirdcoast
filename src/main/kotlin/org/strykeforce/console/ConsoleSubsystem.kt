package org.strykeforce.console

import edu.wpi.first.wpilibj2.command.SubsystemBase
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@ExperimentalStdlibApi
class ConsoleSubsystem @JvmOverloads constructor(var enabled: Boolean = false) : SubsystemBase() {

    private var console: Console = if (enabled) SSD1306Console() else DummyConsole()

    fun clear() = console.clear()

    @JvmOverloads
    fun writeString(string: String, x: Int, y: Int, on: Boolean = true) =
        console.writeString(string, x, y, on)

    @JvmOverloads
    fun writeStringCentered(string: String, y: Int, on: Boolean = true) =
        console.writeStringCentered(string, y, on)

}