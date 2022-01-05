package advent.day.day25

import advent.AdventDay
import advent.day.day25.domain.Position

class AdventDay25 : AdventDay() {
    private val fileText = getFileAsText("day25")

    private val seaCucumberMap = HashMap(
        fileText
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
    )

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
                .sumOf { it }

            val countMovingSouth = southFacingSeaCucumbers
                .filter { canMove(it.key, it.value) }
                .map { move(it.key, it.value) }
                .sumOf { it }

            countMovingSeaCucumbers = countMovingEast + countMovingSouth

            printMap(step, countMovingSeaCucumbers)
        }
    }

    private fun printMap(step: Int, countMovingSeaCucumbers: Int) {
        val list = List(yMax + 1) { y ->
            List(xMax + 1) { x ->
                seaCucumberMap.getOrDefault(Position(x, y), '.')
            }
        }

        println("Map after step $step ($countMovingSeaCucumbers moving sea cucumbers):")
        println("${list.joinToString("\n") { line -> line.joinToString("") }}\n")
    }

    private fun canMove(currentPosition: Position, heard: Char): Boolean {
        return ! seaCucumberMap.containsKey(getNewPosition(currentPosition, heard))
    }

    private fun move(currentPosition: Position, heard: Char): Int {
        val newPosition = getNewPosition(currentPosition, heard)

        seaCucumberMap.remove(currentPosition)
        seaCucumberMap[newPosition] = heard

        return 1
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
