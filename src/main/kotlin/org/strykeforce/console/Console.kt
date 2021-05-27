package org.strykeforce.console

import edu.wpi.first.wpilibj2.command.button.Button

interface Console {
    enum class Switch { CENTER, NORTH, SOUTH, EAST, WEST }

    fun clear()
    fun periodic()
    fun writeString(string: String, x: Int, y: Int, on: Boolean = true)
    fun writeStringCentered(string: String, y: Int, on: Boolean = true)
    fun getSwitch(switch: Switch) : Boolean
}