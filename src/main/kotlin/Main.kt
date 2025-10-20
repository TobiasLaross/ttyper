import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import java.io.File

fun main() {
    val terminal = DefaultTerminalFactory().createTerminal()
    val dictionary = readDictionary()
    val wordsToType = dictionary.joinToString(separator = " ").toCharArray()
    println(wordsToType)
    for (char in wordsToType) {
        var correctKeyPressed = false
        while (!correctKeyPressed) {
            val key = terminal.readInput()
            if (key.character == char) {
                print(key.character)
                correctKeyPressed = true
            }
        }
    }
}

fun readDictionary(): List<String> {
    val input = File("src/main/kotlin/dictionary")
    val dictionary = input.readLines()
    val numberOfWordsToPick = 30
    val words = mutableListOf<String>()
    repeat(numberOfWordsToPick) {
        words.add(dictionary.random().lowercase())
    }
    return words
}