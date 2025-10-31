import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.TextCharacter.fromCharacter
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.terminal.Terminal
import java.io.File
import kotlin.math.roundToInt

fun main() {
    val terminal = DefaultTerminalFactory().createTerminal()
    val screen = TerminalScreen(terminal)
    val colSize = screen.terminalSize.columns
    val rowSize = screen.terminalSize.rows
    val numberOfWordsToType = 20
    val green = TextColor.RGB(100, 200, 100)
    val red = TextColor.RGB(250, 90, 90)
    val white = TextColor.RGB(0, 0, 0)
    val wordsFromFile = readDictionary(numberOfWordsToType).joinToString(separator = " ").toCharArray()
    var timerHasBeenStarted = false
    var startTime: Long = 0
    var errorCount = 0
    val printableWidth = when {
        colSize * 0.7 > 100 -> 100
        colSize * 0.7 > 80 -> 80
        else -> 60
    }
    screen.startScreen()
    val startPosition = (colSize / 2 - printableWidth / 2) to (rowSize / 2)
    val lines = splitCharArrayByWidth(wordsFromFile, printableWidth)
    screen.drawWords(lines, startPosition.first, startPosition.second)
    var letter = 0
    var line = 0
    var cursorPosition = startPosition
    screen.setCursorPosition(cursorPosition.first, cursorPosition.second)
    screen.refresh()
    while (line < lines.size) {
        while (letter < lines[line].size) {
            val key = terminal.readInput()
            if (!timerHasBeenStarted) {
                startTime = System.currentTimeMillis()
                timerHasBeenStarted = true
            }
            if (key.keyType != KeyType.Backspace) {
                if (key.character == lines[line][letter]) {
                    screen.drawCharacter(key.character, cursorPosition, green)
                } else {
                    errorCount++
                    screen.drawCharacter(lines[line][letter], cursorPosition, red)
                }
                cursorPosition = cursorPosition.copy(first = cursorPosition.first + 1)
                screen.setCursorPosition(cursorPosition.first, cursorPosition.second)
                if (letter + 1 == lines[line].size) {
                    //terminal.lineBreak(initialCursorPosition.column)
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
                    //terminal.cursorUp(initialCursorPosition.column + lines[line].size)
                    print("\b${lines[line][letter]}\b")
                }
            }
            screen.refresh()
        }
    }
    val endTime = System.currentTimeMillis()
    val elapsedTimeInSeconds = (endTime - startTime) / 1000
    val wpm = (numberOfWordsToType.toDouble() / elapsedTimeInSeconds.toDouble()) * 60
    val totalCharacters = wordsFromFile.size
    val rawAccuracy = ((totalCharacters - errorCount).toDouble() / totalCharacters.toDouble() * 100)
    val accuracy = rawAccuracy.coerceIn(0.0, 100.0).roundToInt()

    println()
    println("$numberOfWordsToType words typed in $elapsedTimeInSeconds seconds")
    println("WPM: ${wpm.roundToInt()}")
    println("Accuracy: $accuracy%")
    screen.stopScreen()
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

fun Screen.setCursorPosition(column: Int, row: Int) {
   this.cursorPosition = TerminalPosition(column, row)
}

fun Screen.drawWords(words: List<List<Char>>, colPos: Int, rowPos: Int) {
    val text = this.newTextGraphics()
    var row = rowPos
    words.forEach {
        text.putString(colPos, row, it.joinToString(separator = ""))
        row++
    }
   // this.refresh()
}

fun Screen.drawCharacter(char: Char, position: Pair<Int, Int>, color: TextColor.RGB) {
    val tc = fromCharacter(char)
    tc.firstOrNull()?.let {
        this.setCharacter(position.first, position.second, it.withForegroundColor(color))
    }
    //this.refresh()
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
