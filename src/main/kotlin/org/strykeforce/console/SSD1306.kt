@file:Suppress("unused")

package org.strykeforce.console

import edu.wpi.first.wpilibj.I2C
import edu.wpi.first.wpilibj.Timer
import mu.KotlinLogging
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import kotlin.experimental.and
import kotlin.experimental.or


private val logger = KotlinLogging.logger {}

private const val DATA_TRANSFER_SIZE = 64
private const val DISPLAY_ADDRESS = 0x3C // 64-pixel tall

private const val DISPLAY_WIDTH = 128
private const val DISPLAY_HEIGHT = 64
private const val MAX_INDEX = DISPLAY_HEIGHT / 8 * DISPLAY_WIDTH

private const val SSD1306_SET_CONTRAST = 0x81
private const val SSD1306_DISPLAY_ALL_ON_RESUME = 0xA4
private const val SSD1306_DISPLAY_ALL_ON = 0xA5
private const val SSD1306_NORMAL_DISPLAY = 0xA6
private const val SSD1306_INVERT_DISPLAY = 0xA7
private const val SSD1306_DISPLAY_OFF = 0xAE
private const val SSD1306_DISPLAY_ON = 0xAF

private const val SSD1306_SET_DISPLAY_OFFSET = 0xD3
private const val SSD1306_SET_COMP_INS = 0xDA

private const val SSD1306_SET_VCOM_DETECT = 0xDB

private const val SSD1306_SET_DISPLAY_CLOCK_DIV = 0xD5
private const val SSD1306_SET_PRE_CHARGE = 0xD9

private const val SSD1306_SET_MULTIPLEX = 0xA8

private const val SSD1306_SET_LOW_COLUMN = 0x00
private const val SSD1306_SET_HIGH_COLUMN = 0x10

private const val SSD1306_SET_START_LINE = 0x40

private const val SSD1306_MEMORY_MODE = 0x20
private const val SSD1306_COLUMN_ADDR = 0x21
private const val SSD1306_PAGE_ADDR = 0x22

private const val SSD1306_COM_SCAN_INC = 0xC0
private const val SSD1306_COM_SCAN_DEC = 0xC8

private const val SSD1306_SEG_REMAP = 0xA0

private const val SSD1306_CHARGE_PUMP = 0x8D

private const val SSD1306_EXTERNAL_VCC = 0x1
private const val SSD1306_SWITCH_CAP_VCC = 0x2

@ExperimentalStdlibApi
class SSD1306 {
    private val display = I2C(I2C.Port.kOnboard, DISPLAY_ADDRESS)
    private val writeBuffer = ByteArray(DISPLAY_WIDTH * DISPLAY_HEIGHT / 8)
    private val readBuffer = ByteArray(DISPLAY_WIDTH * DISPLAY_HEIGHT / 8)
    private val rotation = Rotation.DEG_0
    private val periodicTimer = Timer()
    private var future: CompletableFuture<Void> = CompletableFuture.completedFuture(null)

    init {
        init()
        periodicTimer.start()
    }

    fun periodic() {
        if (periodicTimer.advanceIfElapsed(0.25) and future.isDone) {
            future = CompletableFuture.runAsync(this::update)
//            logger.debug { "periodicTimer advanced and future is done" }
        }
    }


    val width = when (rotation) {
        Rotation.DEG_90, Rotation.DEG_270 -> DISPLAY_HEIGHT
        Rotation.DEG_0, Rotation.DEG_180 -> DISPLAY_WIDTH
    }

    val height = when (rotation) {
        Rotation.DEG_90, Rotation.DEG_270 -> DISPLAY_WIDTH
        Rotation.DEG_0, Rotation.DEG_180 -> DISPLAY_HEIGHT
    }

    private fun writeCommand(command: Int) {
        if (display.write(0x00, command)) logger.error { "I2C transfer aborted for command: $command" }
    }

    fun setPixel(x: Int, y: Int, on: Boolean) {
        when (rotation) {
            Rotation.DEG_0 -> updateImageBuffer(x, y, on)
            Rotation.DEG_90 -> updateImageBuffer(y, width - x - 1, on)
            Rotation.DEG_180 -> updateImageBuffer(width - x - 1, height - y - 1, on)
            Rotation.DEG_270 -> updateImageBuffer(height - y - 1, x, on)
        }
    }

    fun drawChar(c: Char, font: Font, x: Int, y: Int, on: Boolean) {
        font.drawChar(this, c, x, y, on)
    }

    fun drawString(string: String, font: Font, x: Int, y: Int, on: Boolean) {
        var posX = x
        var posY = y
        for (c in string.toCharArray()) {
            if (c == '\n') {
                posY += font.outerHeight
                posX = x
            } else {
                if (posX >= 0 && posX + font.width < width && posY >= 0 && posY + font.height < height) {
                    drawChar(c, font, posX, posY, on)
                }
                posX += font.outerWidth
            }
        }
    }

    fun drawStringCentered(string: String, font: Font, y: Int, on: Boolean) {
        val strSizeX = string.length * font.outerWidth
        val x: Int = (width - strSizeX) / 2
        drawString(string, font, x, y, on)
    }

    fun clearRect(x: Int, y: Int, width: Int, height: Int, on: Boolean) {
        for (posX in x until x + width) {
            for (posY in y until y + height) {
                setPixel(posX, posY, on)
            }
        }
    }

    fun clear() {
        synchronized(this) {
            Arrays.fill(writeBuffer, 0x00.toByte())
        }
    }

    fun update() {
        synchronized(this) {
            System.arraycopy(writeBuffer, 0, readBuffer, 0, writeBuffer.size)
        }
        val setupCommands = byteArrayOfInt(0, SSD1306_COLUMN_ADDR, 0, DISPLAY_WIDTH - 1, SSD1306_PAGE_ADDR, 0, 7)
        if (display.writeBulk(setupCommands)) logger.warn { "I2C command transfer interrupted" }
        val chunk = ByteArray(DATA_TRANSFER_SIZE + 1)
        chunk[0] = 0x40
        for (i in 0 until DISPLAY_WIDTH * DISPLAY_HEIGHT / 8 / DATA_TRANSFER_SIZE) {
            val start = i * DATA_TRANSFER_SIZE
            readBuffer.copyInto(chunk, 1, start, start + DATA_TRANSFER_SIZE)
            if (display.writeBulk(chunk)) logger.warn { "I2C data transfer interrupted" }
        }
    }

    private fun init() {
        writeCommand(SSD1306_DISPLAY_OFF)
        writeCommand(SSD1306_SET_DISPLAY_CLOCK_DIV)
        writeCommand(0x80) // default freq (8) and divide (0)
        writeCommand(SSD1306_SET_MULTIPLEX)
        writeCommand((DISPLAY_HEIGHT - 1))
        writeCommand(SSD1306_SET_DISPLAY_OFFSET)
        writeCommand(0) // default 0
        writeCommand((SSD1306_SET_START_LINE or 0x0)) // default 0
        writeCommand(SSD1306_CHARGE_PUMP)
        writeCommand(0x14) // enable display charge pump, internal Vcc
        writeCommand(SSD1306_MEMORY_MODE)
        writeCommand(0) // horizontal addressing
        writeCommand((SSD1306_SEG_REMAP or 0x1)) // column address 127 mapped to SEG0
        writeCommand(SSD1306_COM_SCAN_DEC) // scan from COM[N-1] to COM0
        writeCommand(SSD1306_SET_COMP_INS)
        writeCommand(0x12) // default: alt. COM pin conf, disable LR remap, 128x64
        writeCommand(SSD1306_SET_CONTRAST)
        writeCommand(0xCF) // increased from default, internal Vcc
        writeCommand(SSD1306_SET_PRE_CHARGE)
        writeCommand(0xF1) // P1 period 15 DCLKs, P2 period 1 DCLK, internal Vcc
        writeCommand(SSD1306_SET_VCOM_DETECT)
        writeCommand(0x40)
        writeCommand(SSD1306_DISPLAY_ALL_ON_RESUME)
        writeCommand(SSD1306_NORMAL_DISPLAY)
        writeCommand(SSD1306_DISPLAY_ON)

        clear()
        update()
    }

    private fun updateImageBuffer(x: Int, y: Int, on: Boolean) {
        val pos = x + y / 8 * DISPLAY_WIDTH
        if (pos in 0 until MAX_INDEX) {
            synchronized(this) {
                writeBuffer[pos] = if (on) {
                    writeBuffer[pos] or (1 shl (y and 0x07)).toByte()
                } else {
                    writeBuffer[pos] and (1 shl (y and 0x07)).inv().toByte()
                }
            }
        }
    }

    enum class Rotation {
        DEG_0, DEG_90, DEG_180, DEG_270
    }
}
