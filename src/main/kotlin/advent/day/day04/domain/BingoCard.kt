package advent.day.day04.domain

import java.lang.StrictMath.sqrt

class BingoCard(numbers: String) {
    var card = numbers
        .replace("\n", " ")
        .replace("  ", " ")
        .split(" ")
        .filter { it.matches(Regex("\\d+")) }
        .map { it.toInt() to false }

    private val gridSize = sqrt(card.size.toDouble()).toInt()

    fun markNumber(drawnNumber: Int) {
        this.card = this.card.map { if (drawnNumber == it.first) Pair(it.first, true) else it }
    }

    fun hasWon(): Boolean {
        val horizontalLines = this.card.windowed(gridSize, gridSize)
        val verticalLines = (0 until gridSize)
            .map { i -> this.card.slice((i until this.card.size step 5)) }

        return (horizontalLines + verticalLines)
            .any { it.all { pair -> pair.second } }
    }

    fun getScore(drawnNumber: Int): Int {
        return drawnNumber * this.card.filter { !it.second }.sumOf { it.first }
    }
}
