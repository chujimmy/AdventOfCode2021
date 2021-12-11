package advent.day.day04.domain

import java.lang.StrictMath.sqrt
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class BingoCard(numbers: String) {

    val card: Set<Int> = LinkedHashSet(
        numbers
            .replace("  ", " ")
            .split(Pattern.compile("[\n( ){2}]"))
            .filter { it.matches(Regex("\\d+")) }
            .map { it.toInt() }
    )
    var isWon: Boolean = false
    private val gridSize: Int = sqrt(card.size.toDouble()).toInt()
    private val markedNumber: MutableList<Int> = ArrayList()

    fun markCard(drawnNumber: Int): Boolean {
        // Check syntax / variable name ?
        val numberIndex = this.card.indexOf(drawnNumber)

        if (numberIndex == -1) {
            return false
        }

        this.markedNumber.add(numberIndex)
        return true
    }

    fun isComplete(): Boolean {
        if (this.markedNumber.size < this.gridSize) {
            return false
        }

        val horizontalLine = IntArray(this.gridSize){it}
        val verticalLine = IntArray(this.gridSize){ it * this.gridSize}

        for (i in 0 until this.gridSize) {
            val lineToCheck = horizontalLine.map { it + i * this.gridSize }
            val columnToCheck = verticalLine.map { it + i }

            if (this.markedNumber.containsAll(lineToCheck)) {
                this.isWon = true
                return true
            }

            if (this.markedNumber.containsAll(columnToCheck)) {
                this.isWon = true;
                return true
            }
        }

        return false
    }

    fun getScore(drawnNumber: Int): Int {
        return drawnNumber * this.card.filterIndexed{ i, _ ->
            !this.markedNumber.contains(i)
        }.toList().sumOf{it}
    }
}