package advent.day.day04

import advent.AdventDay
import advent.day.day04.domain.BingoCard
import java.util.regex.Pattern

class AdventDay04 : AdventDay() {
    override fun run() {
        val fileText = getFileAsText("day04")
        val fileContent = fileText.split(Pattern.compile("\n\n"))

        if (fileContent.size < 2) {
            return
        }

        val drawnNumbers = fileContent[0].split(",").map { it.toInt() }
        val bingoCards = fileContent.subList(1, fileContent.size).map { BingoCard(it) }

        println("Getting Ready!")

        drawnNumbers.forEach { number ->
            bingoCards.forEachIndexed { i, card ->
                if (! card.isWon) {

                    val isNumberOnCard = card.markCard(number)
                    if (isNumberOnCard && card.isComplete()) {
                        val score = card.getScore(number)
                        println("We have a winner after number $number was drawn")
                        println("Card ${i + 1} with score: $score")
                        println("Card: ${card.card}")
                    }
                }
            }
        }
    }
}
