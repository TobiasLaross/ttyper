package org.example

import org.jline.terminal.Attributes
import org.jline.terminal.TerminalBuilder
import org.jline.utils.AttributedString
import org.jline.utils.AttributedStyle
import org.jline.utils.InfoCmp
import org.jline.utils.InputStreamReader
import java.util.logging.ConsoleHandler
import java.util.logging.Level
import java.util.logging.Logger

fun logg() {
    val handler = ConsoleHandler()
    handler.level = Level.FINE

    val logger: Logger = Logger.getLogger("org.jline")
    logger.setLevel(Level.FINE)
    logger.addHandler(handler)
}

fun main() {
    logg()
    val t =
        TerminalBuilder
            .builder()
            .system(true)
            .ffm(true)
            .jna(true)
            .dumb(false)
            .build()

    t.attributes =
        t.attributes.apply {
            setLocalFlag(Attributes.LocalFlag.ICANON, false)
            setLocalFlag(Attributes.LocalFlag.ECHO, false)
            setControlChar(Attributes.ControlChar.VMIN, 1)
            setControlChar(Attributes.ControlChar.VTIME, 0)
        }
    val r = InputStreamReader(t.input())
    // val r = t.reader()
    val w = t.writer()

    // Clear the screen
    t.puts(InfoCmp.Capability.clear_screen)
    t.flush()

    val colored: AttributedString = AttributedString("This text is blue", AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE))
    colored.println(t)

    // move cursor up to overwrite the prompt when ttyping
    t.puts(InfoCmp.Capability.cursor_up)
    t.flush()

    while (true) {
        val e = r.read()
        if (e == 127) {
            t.puts(InfoCmp.Capability.cursor_left)
        } else {
            w.print(Char(e))
        }
        // Save cursor position
        t.puts(InfoCmp.Capability.save_cursor)
        t.puts(InfoCmp.Capability.cursor_down)
        t.puts(InfoCmp.Capability.cursor_down)
        t.puts(InfoCmp.Capability.clr_eol)
        w.print(e)
        t.puts(InfoCmp.Capability.restore_cursor)
        w.flush()
    }
}
