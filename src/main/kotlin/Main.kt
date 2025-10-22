import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import java.io.File
import kotlin.math.roundToInt

fun main() {
    val terminal = DefaultTerminalFactory().createTerminal()
    val numberOfWordsToType = 20
    val dictionary = readDictionary(numberOfWordsToType)
    val wordsToType = dictionary.joinToString(separator = " ").toCharArray()
    var timerHasBeenStarted = false
    var startTime: Long = 0
    terminal.clearScreen()
    terminal.cursorPosition =
        terminal.cursorPosition.withRow(terminal.terminalSize.rows / 2).withColumn(terminal.terminalSize.columns / 5)
    val initialCursorPosition = terminal.cursorPosition
    // todo: separate each lines into separate array to track cursor movement when typing
    for (word in dictionary) {
        val wordPlusSpace = "$word "
        if (terminal.cursorPosition.column + wordPlusSpace.length > (terminal.terminalSize.columns / 5) * 4) {
            terminal.cursorPosition = terminal.cursorPosition.withRow(initialCursorPosition.row + 1)
                .withColumn(initialCursorPosition.column)
        }
        print(wordPlusSpace)
    }
    terminal.cursorPosition = initialCursorPosition
    // todo: figure out something better here
    for (char in wordsToType) {
        do {
            val key = terminal.readInput()
            if (!timerHasBeenStarted) {
                startTime = System.currentTimeMillis()
                timerHasBeenStarted = true
            }
            if (key.keyType != KeyType.Backspace) {
                if (key.character == char) {
                    terminal.setForegroundColor(TextColor.RGB(0, 180, 0))
                    print(key.character)
                    terminal.resetColorAndSGR()
                } else {
                    terminal.setForegroundColor(TextColor.RGB(200, 0, 0))
                    print(char)
                    terminal.resetColorAndSGR()
                }
            }
        } while (key.keyType == KeyType.Backspace)
    }
    val endTime = System.currentTimeMillis()
    val elapsedTimeInSeconds = (endTime - startTime) / 1000
    val wpm = (numberOfWordsToType.toDouble() / elapsedTimeInSeconds.toDouble()) * 60
    println()
    println("$numberOfWordsToType words typed in $elapsedTimeInSeconds seconds")
    println("WPM: ${wpm.roundToInt()}")
}

fun readDictionary(numberOfWordsToType: Int): List<String> {
    val input = File("src/main/kotlin/dictionary")
    val dictionary = input.readLines()
    val words = mutableListOf<String>()
    repeat(numberOfWordsToType) {
        words.add(dictionary.random().lowercase())
    }
    return words
}