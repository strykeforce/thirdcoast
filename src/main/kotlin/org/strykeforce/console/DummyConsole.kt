package org.strykeforce.console

import mu.KotlinLogging
import org.strykeforce.console.Console.Switch

private val logger = KotlinLogging.logger {}

class DummyConsole : Console {

    init {
        logger.info { "console logging is disabled" }
    }

    override fun clear() {}

    override fun periodic() {}

    override fun writeString(string: String, x: Int, y: Int, on: Boolean) {}

    override fun writeStringCentered(string: String, y: Int, on: Boolean) {}

    override fun getSwitch(switch: Switch) = false
}