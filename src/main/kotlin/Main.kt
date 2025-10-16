import com.googlecode.lanterna.terminal.DefaultTerminalFactory

fun main() {
    val terminal = DefaultTerminalFactory().createTerminal()
    while (true) {
        val key = terminal.readInput()
        println("${key.character}")
    }
}