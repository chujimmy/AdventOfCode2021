package advent.day.day04

import advent.AdventDay
import advent.day.day04.domain.BingoCard
import java.util.regex.Pattern

class AdventDay04 : AdventDay() {
    private val fileText = getFileAsText("day04")
    private val fileContent = fileText.split(Pattern.compile("\n\n"))
    private val drawnNumbers = fileContent[0].split(",").map { it.toInt() }

    private val bingoCards = fileContent.subList(1, fileContent.size)
        .map { it.replace("\n", " ").replace("  ", " ") }
        .map { it.split(" ") }
        .map { it.filter { n -> n.matches(Regex("\\d+")) }.map { n -> Pair(n.toInt(), false) } }
        .map { BingoCard(it) }

    override fun run() {
        drawnNumbers.forEach { number ->
            bingoCards.forEach { card ->
                if (!card.hasWon()) {
                    card.markNumber(number)

                    if (card.hasWon()) {
                        println("Winning Card With Score: ${card.getScore(number)}")
                    }
                }
            }
        }
    }
}
