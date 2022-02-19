package advent.day.day25

import advent.AdventDay
import advent.day.day25.domain.Position

class AdventDay25 : AdventDay() {
    private val fileText = getFileAsText("day25")

    private val seaCucumberMap = fileText
        .split("\n")
        .flatMapIndexed { y, hLine ->
            hLine.mapIndexed { x, seaCucumber ->
                if (seaCucumber != '.') {
                    Pair(Position(x, y), seaCucumber)
                } else {
                    null
                }
            }
        }
        .filterNotNull()
        .associate { it.first to it.second }
        .toMutableMap()

    private val xMax = fileText.split("\n").maxOf { it.length } - 1
    private val yMax = fileText.split("\n").size - 1

    override fun run() {
        var countMovingSeaCucumbers = 1
        var step = 0

        while (countMovingSeaCucumbers > 0) {
            step++
            val eastFacingSeaCucumbers = seaCucumberMap.filter { it.value == '>' }
            val southFacingSeaCucumbers = seaCucumberMap.filter { it.value == 'v' }

            val countMovingEast = eastFacingSeaCucumbers
                .filter { canMove(it.key, it.value) }
                .map { move(it.key, it.value) }
                .count()

            val countMovingSouth = southFacingSeaCucumbers
                .filter { canMove(it.key, it.value) }
                .map { move(it.key, it.value) }
                .count()

            countMovingSeaCucumbers = countMovingEast + countMovingSouth

            println("After step $step, $countMovingSeaCucumbers moving sea cucumbers")
        }
    }

    private fun canMove(currentPosition: Position, heard: Char): Boolean {
        return ! seaCucumberMap.containsKey(getNewPosition(currentPosition, heard))
    }

    private fun move(currentPosition: Position, heard: Char) {
        val newPosition = getNewPosition(currentPosition, heard)

        seaCucumberMap.remove(currentPosition)
        seaCucumberMap[newPosition] = heard
    }

    private fun getNewPosition(currentPosition: Position, heard: Char): Position {
        return when (heard) {
            'v' -> {
                if (currentPosition.y == yMax) {
                    Position(currentPosition.x, 0)
                } else {
                    Position(currentPosition.x, currentPosition.y + 1)
                }
            }
            '>' -> {
                if (currentPosition.x == xMax) {
                    Position(0, currentPosition.y)
                } else {
                    Position(currentPosition.x + 1, currentPosition.y)
                }
            }
            else -> currentPosition
        }
    }
}
