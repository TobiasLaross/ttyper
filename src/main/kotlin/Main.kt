import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.terminal.Terminal
import java.io.File
import kotlin.math.roundToInt
import java.util.ArrayDeque

fun main() {
    val terminal = DefaultTerminalFactory().createTerminal()
    val numberOfWordsToType = 20
    val dictionary = readDictionary(numberOfWordsToType)
    val wordsToType = dictionary.joinToString(separator = " ").toCharArray()
    var timerHasBeenStarted = false
    var startTime: Long = 0
    terminal.clearScreen()
    val printableWidth = when {
        terminal.terminalSize.columns * 0.7 > 100 -> 100
        terminal.terminalSize.columns * 0.7 > 80 -> 80
        else -> 60
    }
    terminal.cursorPosition = terminal.cursorPosition
        .withRow(terminal.terminalSize.rows / 2)
        .withColumn((terminal.terminalSize.columns / 2) - printableWidth / 2)

    val initialCursorPosition = terminal.cursorPosition
    val lineBreakIndexStack = ArrayDeque<TerminalPosition>()
    for (word in dictionary) {
        val wordPlusSpace = "$word "
        if (terminal.cursorPosition.column + wordPlusSpace.length > initialCursorPosition.column + printableWidth) {
            lineBreakIndexStack.addLast(terminal.cursorPosition)
            terminal.lineBreak(initialCursorPosition.column)
        }
        print(wordPlusSpace)
    }
    terminal.cursorPosition = initialCursorPosition
    var i = 0
    while (i < wordsToType.size) {
        val key = terminal.readInput()
        if (!timerHasBeenStarted) {
            startTime = System.currentTimeMillis()
            timerHasBeenStarted = true
        }
        if (key.keyType != KeyType.Backspace) {
            if (key.character == wordsToType[i]) {
                terminal.setForegroundColor(TextColor.RGB(0, 180, 0))
                print(key.character)
                terminal.resetColorAndSGR()
            } else {
                terminal.setForegroundColor(TextColor.RGB(200, 0, 0))
                print(wordsToType[i])
                terminal.resetColorAndSGR()
            }
            if (terminal.cursorPosition == lineBreakIndexStack.firstOrNull()) {
                terminal.lineBreak(initialCursorPosition.column)
                lineBreakIndexStack.removeFirst()
            }
            i++
        } else {
            if (i > 0) {
                i--
                print("\b${wordsToType[i]}\b")
            }
        }
    }
    val endTime = System.currentTimeMillis()
    val elapsedTimeInSeconds = (endTime - startTime) / 1000
    val wpm = (numberOfWordsToType.toDouble() / elapsedTimeInSeconds.toDouble()) * 60
    println()
    println("$numberOfWordsToType words typed in $elapsedTimeInSeconds seconds")
    println("WPM: ${wpm.roundToInt()}")
}

fun Terminal.lineBreak(initialColumn: Int) {
    this.cursorPosition = this.cursorPosition
        .withRow(this.cursorPosition.row + 1)
        .withColumn(initialColumn)
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