package org.strykeforce.console

interface Console {
    enum class Switch { CENTER, NORTH, SOUTH, EAST, WEST }

    fun clear()
    fun periodic()
    fun writeString(string: String, x: Int, y: Int, on: Boolean = true)
    fun writeStringCentered(string: String, y: Int, on: Boolean = true)
    fun getSwitch(switch: Switch): Boolean
}

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

class DummyConsole : Console {

    override fun clear() {}
    override fun periodic() {}
    override fun writeString(string: String, x: Int, y: Int, on: Boolean) {}
    override fun writeStringCentered(string: String, y: Int, on: Boolean) {}
    override fun getSwitch(switch: Console.Switch) = false
}