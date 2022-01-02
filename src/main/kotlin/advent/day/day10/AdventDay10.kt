package advent.day.day10

import advent.AdventDay
import java.util.Stack

class AdventDay10 : AdventDay() {
    private val fileText = getFileAsText("day10")
    private val navigationSubsystem = fileText
        .split("\n")
        .toList()

    private val pairs = setOf(
        Pair('(', ')'),
        Pair('[', ']'),
        Pair('{', '}'),
        Pair('<', '>'),
    )

    override fun run() {
        val scores: List<Pair<Int, Long>> = navigationSubsystem.map { line ->
            val stack = Stack<Char>()
            var scorePair: Pair<Int, Long>? = null

            for (char in line) {
                when (char) {
                    '(', '[', '{', '<' -> stack.push(char)
                    ')', ']', '}', '>' -> {
                        val score = getSyntaxErrorScore(stack, char)
                        if (score > 0) {
                            scorePair = Pair(score, 0)
                            break
                        }
                    }
                }
            }

            scorePair ?: Pair(0, stack.foldRight(0L) { c, acc -> (acc * 5L) + getAutocompleteScore(c) })
        }

        val syntaxErrorScore = scores.filter { it.first != 0 }.sumOf { it.first }
        println("Syntax error score: $syntaxErrorScore")
        val autoCompleteScores = scores.filter { it.second != 0L }.map { it.second }.sortedBy { it }
        val autoCompleteMiddleScore = autoCompleteScores[autoCompleteScores.size / 2]
        println("Syntax error score: $autoCompleteMiddleScore")
    }

    private fun getSyntaxErrorScore(stack: Stack<Char>, char: Char): Int {
        if (stack.isEmpty()) return 0

        if (pairs.contains(Pair(stack.peek(), char))) {
            stack.pop()
            return 0
        }

        return when (char) {
            ')' -> 3
            ']' -> 57
            '}' -> 1197
            '>' -> 25137
            else -> 0
        }
    }

    private fun getAutocompleteScore(char: Char): Long {
        return when (char) {
            '(' -> 1L
            '[' -> 2L
            '{' -> 3L
            '<' -> 4L
            else -> 0L
        }
    }
}
