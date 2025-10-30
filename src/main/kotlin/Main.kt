import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.terminal.Terminal
import java.io.File
import kotlin.math.roundToInt

fun main() {
    val terminal = DefaultTerminalFactory().createTerminal()
    val numberOfWordsToType = 20
    val wordsFromFile = readDictionary(numberOfWordsToType).joinToString(separator = " ").toCharArray()
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
    val lines = splitCharArrayByWidth(wordsFromFile, printableWidth)
    lines.forEach {
        print(it.joinToString(separator = ""))
        terminal.lineBreak(initialCursorPosition.column)
    }
    terminal.cursorPosition = initialCursorPosition
    var letter = 0
    var line = 0
    while (line < lines.size) {
        while (letter < lines[line].size) {
            val key = terminal.readInput()
            if (!timerHasBeenStarted) {
                startTime = System.currentTimeMillis()
                timerHasBeenStarted = true
            }
            if (key.keyType != KeyType.Backspace) {
                if (key.character == lines[line][letter]) {
                    terminal.setForegroundColor(TextColor.RGB(100, 200, 100))
                    print(key.character)
                    terminal.resetColorAndSGR()
                } else {
                    terminal.setForegroundColor(TextColor.RGB(250, 90, 90))
                    print(lines[line][letter])
                    terminal.resetColorAndSGR()
                }
                if (letter + 1 == lines[line].size) {
                    terminal.lineBreak(initialCursorPosition.column)
                    letter = 0
                    line++
                    break
                } else {
                    letter++
                }
            } else {
                if (letter > 0) {
                    letter--
                    print("\b${lines[line][letter]}\b")
                } else if (line > 0) {
                    line--
                    letter = lines[line].size - 1
                    terminal.cursorUp(initialCursorPosition.column + lines[line].size)
                    print("\b${lines[line][letter]}\b")
                }
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

fun Terminal.cursorUp(columnEnd: Int) {
    this.cursorPosition = this.cursorPosition
        .withRow(this.cursorPosition.row - 1)
        .withColumn(columnEnd)
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

fun splitCharArrayByWidth(input: CharArray, maxPrintableWidth: Int): List<List<Char>> {
    val result = mutableListOf<List<Char>>()
    var startIndex = 0

    while (startIndex < input.size) {
        var endIndex = (startIndex + maxPrintableWidth).coerceAtMost(input.size)

        if (endIndex < input.size) {
            var lastWhitespace = -1
            for (i in startIndex until endIndex) {
                if (input[i].isWhitespace()) {
                    lastWhitespace = i
                }
            }
            if (lastWhitespace != -1) {
                endIndex = lastWhitespace + 1
            }
        }

        result.add(input.slice(startIndex until endIndex))
        startIndex = endIndex
    }

    return result
}