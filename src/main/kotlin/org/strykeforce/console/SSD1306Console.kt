package org.strykeforce.console

@ExperimentalStdlibApi
class SSD1306Console : Console {
    val display = SSD1306()

    override fun clear() = display.clear()

    override fun periodic() = display.periodic()

    override fun writeString(string: String, x: Int, y: Int, on: Boolean) =
        display.drawString(string, Font.FONT_5X8, x, y, on)

    override fun writeStringCentered(string: String, y: Int, on: Boolean) =
        display.drawStringCentered(string, Font.FONT_5X8, y, on)
}