package org.strykeforce.console

import edu.wpi.first.wpilibj.I2C
import edu.wpi.first.wpilibj.Timer
import mu.KotlinLogging
import org.strykeforce.console.Console.Switch.*

private val logger = KotlinLogging.logger {}

private const val EXPANDER_ADDRESS = 0x20

private const val POLL_SECONDS = 100.0 / 1000.0

private const val IODIR = 0x00 // IO direction
private const val GPPU = 0x06 // GPIO pull-up
private const val GPIO = 0x09
//private const val IPOL = 0x01
//private const val GPINTEN = 0x02
//private const val DEFVAL = 0x03
//private const val INTCON = 0x04
//private const val IOCON = 0x05
//private const val INTF = 0x07
//private const val INTCAP = 0x08
//private const val OLAT = 0x0A

private const val SWITCH_PORTS = 0b11111111
private const val SWITCH_CENTER: UByte = 0b00010000u
private const val SWITCH_NORTH: UByte = 0b00000001u
private const val SWITCH_SOUTH: UByte = 0b00000100u
private const val SWITCH_EAST: UByte = 0b00001000u
private const val SWITCH_WEST: UByte = 0b00000010u
private const val SWITCH_ONE: UByte = 0b00100000u
private const val SWITCH_TWO: UByte = 0b01000000u
private const val SWITCH_THREE: UByte = 0b10000000u
private const val ZERO: UByte = 0u
private const val TRANSFER_ABORT = "I2C data transfer aborted"

class MCP23008 {
    private val expander = I2C(I2C.Port.kOnboard, EXPANDER_ADDRESS)
    private val timer = Timer()
    private var switchState: UByte = 0u

    init {
        init()
        timer.start()
    }

    private fun init() {
        if (expander.write(IODIR, SWITCH_PORTS)) logger.warn { TRANSFER_ABORT }
        if (expander.write(GPPU, SWITCH_PORTS)) logger.warn { TRANSFER_ABORT }
    }

    fun getSwitch(switch: Console.Switch): Boolean {
        val buffer = ByteArray(1)
        if (timer.advanceIfElapsed(POLL_SECONDS)) {
            expander.read(GPIO, buffer.size, buffer)
            switchState = buffer[0].toUByte()
//            logger.debug { switchState.toUByte().toString(2) }
        }
        return ZERO == when (switch) {
            CENTER -> switchState and SWITCH_CENTER
            NORTH -> switchState and SWITCH_NORTH
            SOUTH -> switchState and SWITCH_SOUTH
            EAST -> switchState and SWITCH_EAST
            WEST -> switchState and SWITCH_WEST
            ONE -> switchState and SWITCH_ONE
            TWO -> switchState and SWITCH_TWO
            THREE -> switchState and SWITCH_THREE
        }
    }
}
