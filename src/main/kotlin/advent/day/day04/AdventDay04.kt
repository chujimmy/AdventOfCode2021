package advent.day.day04

import advent.AdventDay
import advent.day.day04.domain.BingoCard
import java.util.regex.Pattern

class AdventDay04 : AdventDay() {
    private val fileText = getFileAsText("day04")
    private val fileContent = fileText.split(Pattern.compile("\n\n"))
    private val drawnNumbers = fileContent[0].split(",").map { it.toInt() }
    private val bingoCards = fileContent.subList(1, fileContent.size).map { BingoCard(it) }

    override fun run() {
        println("Getting Ready!")

        drawnNumbers.forEach { number ->
            bingoCards.forEachIndexed { i, card ->
                if (!card.hasWon()) {
                    card.markNumber(number)

                    if (card.hasWon()) {
                        println("Winning Card after number $number was drawn")
                        println("Winning Card #${i + 1} with score: ${card.getScore(number)}\n")
                    }
                }
            }
        }
    }
}
