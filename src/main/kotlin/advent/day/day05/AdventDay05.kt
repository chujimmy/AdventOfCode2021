package advent.day.day05

import advent.AdventDay
import advent.day.day05.domain.Coordinates
import advent.day.day05.domain.Direction
import java.lang.Integer.max
import java.lang.Integer.min

class AdventDay05 : AdventDay() {
    private val fileText = getFileAsText("day05")
    private val ventMovements = fileText.split("\n")
        .map { it.split(" -> ") }
        .map { movement -> Pair(Coordinates.fromString(movement[0]), Coordinates.fromString(movement[1])) }

    private val furthestPoint = ventMovements
        .fold(0) { acc, movement ->
            listOf(
                acc, movement.first.x, movement.first.y, movement.second.x, movement.second.y
            ).maxOrNull() ?: 0
        }

    private val furthestX = ventMovements.fold(0) { acc, m -> max(acc, max(m.first.x, m.second.x)) }
    private val furthestY = ventMovements.fold(0) { acc, m -> max(acc, max(m.first.y, m.second.y)) }

    override fun run() {
        runPart01()
        runPart02()
    }

    private fun runPart01() {
        val count = countDangerousAreas(false)
        println("Dangerous areas Part 1: $count")
    }

    private fun runPart02() {
        val count = countDangerousAreas(true)
        println("Dangerous areas Part 2: $count")
    }

    private fun countDangerousAreas(useDiagonal: Boolean): Int {
        val diagram = List(furthestY + 1) { IntArray(furthestX + 1) { 0 } }

        ventMovements.forEach { movement ->
            val start = movement.first
            val end = movement.second

            val startingX = min(start.x, end.x)
            val endingX = max(start.x, end.x)
            val startingY = min(start.y, end.y)
            val endingY = max(start.y, end.y)

            when (start.calculateDirection(end)) {
                Direction.VERTICAL -> {
                    (startingY..endingY).forEach { y ->
                        diagram[y][start.x]++
                    }
                }
                Direction.HORIZONTAL -> {
                    (startingX..endingX).forEach { x ->
                        diagram[start.y][x]++
                    }
                }
                Direction.DIAGONAL -> {
                    if (useDiagonal) {
                        val diagonalLength = endingY - startingY
                        val direction = start.calculateDiagonalDirection(end)
                        (0..diagonalLength).forEach { i ->
                            val x = start.x + (i * direction.first)
                            val y = start.y + (i * direction.second)
                            diagram[y][x]++
                        }
                    }
                }
                else -> {}
            }
        }

        return diagram.sumOf { hLine -> hLine.count { it >= 2 } }
    }
}
