package org.strykeforce.console

import edu.wpi.first.wpilibj2.command.button.Button

@ExperimentalStdlibApi
class ConsoleImpl : Console {
    val display = SSD1306()
    val expander = MCP23008()

    override fun clear() = display.clear()

    override fun periodic() = display.periodic()

    override fun writeString(string: String, x: Int, y: Int, on: Boolean) =
        display.drawString(string, Font.FONT_5X8, x, y, on)

    override fun writeStringCentered(string: String, y: Int, on: Boolean) =
        display.drawStringCentered(string, Font.FONT_5X8, y, on)

    override fun getSwitch(switch: Console.Switch) = expander.getSwitch(switch)
}