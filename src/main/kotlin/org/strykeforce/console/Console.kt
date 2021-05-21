package org.strykeforce.console

interface Console {
    fun clear()
    fun periodic()
    fun writeString(string: String, x: Int, y: Int, on: Boolean = true)
    fun writeStringCentered(string: String, y: Int, on: Boolean = true)
}