package advent.day.day10

import advent.AdventDay
import java.util.Stack

class AdventDay10 : AdventDay() {
    private val fileText = getFileAsText("day10")
    private val navigationSubsystem = fileText
        .split("\n")
        .toList()

    private val pairs = mapOf(
        ')' to '(',
        ']' to '[',
        '}' to '{',
        '>' to '<',
    )

    private val syntaxErrorScore = mapOf(
        ')' to 3L,
        ']' to 57L,
        '}' to 1197L,
        '>' to 25137L,
    )

    private val autoCompleteScore = mapOf(
        '(' to 1L,
        '[' to 2L,
        '{' to 3L,
        '<' to 4L,
    )

    override fun run() {
        val scores = navigationSubsystem.map { line ->
            val stack = Stack<Char>()
            var scorePair: Pair<Long, Long>? = null

            for (char in line) {
                when (char) {
                    '(', '[', '{', '<' -> stack.push(char)
                    ')', ']', '}', '>' -> {
                        when {
                            stack.isEmpty() -> {}
                            stack.last() == pairs.getValue(char) -> { stack.pop() }
                            else -> { scorePair = Pair(syntaxErrorScore.getValue(char), 0L); break }
                        }
                    }
                }
            }

            scorePair ?: Pair(0L, stack.foldRight(0L) { c, acc -> (acc * 5L) + autoCompleteScore.getValue(c) })
        }

        val syntaxErrorScore = scores.filter { it.first != 0L }.sumOf { it.first }
        println("Syntax error score: $syntaxErrorScore")
        val autoCompleteScores = scores.filter { it.second != 0L }.map { it.second }.sortedBy { it }
        val autoCompleteMiddleScore = autoCompleteScores[autoCompleteScores.size / 2]
        println("Autocomplete middle score: $autoCompleteMiddleScore")
    }
}
