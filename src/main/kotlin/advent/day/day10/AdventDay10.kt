package advent.day.day10

import advent.AdventDay
import java.util.Stack

class AdventDay10 : AdventDay() {
    private val pairs = setOf(
        Pair('(', ')'),
        Pair('[', ']'),
        Pair('{', '}'),
        Pair('<', '>'),
    )

    override fun run() {
        val fileText = getFileAsText("day10")

        val navigationSubsystem: Array<String> = fileText
            .split("\n")
            .toTypedArray()

        var syntaxErrorTotal = 0
        val autoCompleteScores = mutableListOf<Long>()
        navigationSubsystem.forEach { line ->
            val stack = Stack<Char>()
            run lit@{
                line.forEach { char ->

                    when (char) {
                        '(', '[', '{', '<' -> stack.push(char)
                        ')', ']', '}', '>' -> {
                            val syntaxError = checkCorruptedLine(stack, char)
                            if (syntaxError > 0) {
                                syntaxErrorTotal += syntaxError
                                return@lit
                            }
                        }
                    }
                }

                var autoCompleteTotal: Long = 0
                while (stack.isNotEmpty()) {
                    val char = stack.pop() ?: throw Exception("")

                    val point = when (char) {
                        '(' -> 1
                        '[' -> 2
                        '{' -> 3
                        '<' -> 4
                        else -> 0
                    }

                    autoCompleteTotal = (autoCompleteTotal * 5) + point
                }

                autoCompleteScores.add(autoCompleteTotal)
            }
        }

        val middleScore = autoCompleteScores.sortedBy { it }[(autoCompleteScores.size / 2)]

        println("Middle score: $middleScore")
    }

    private fun checkCorruptedLine(stack: Stack<Char>, char: Char): Int {
        val openingChunkChar = stack.peek() ?: return 0

        if (pairs.contains(Pair(openingChunkChar, char))) {
            stack.pop()
        }

        return when (char) {
            ')' -> if (openingChunkChar == '(') 0 else 3
            ']' -> if (openingChunkChar == '[') 0 else 57
            '}' -> if (openingChunkChar == '{') 0 else 1197
            '>' -> if (openingChunkChar == '<') 0 else 25137
            else -> 0
        }
    }
}
