package advent.day.day05

import advent.AdventDay
import advent.day.day05.domain.Coordinates
import advent.day.day05.domain.Direction
import java.lang.Integer.max
import java.lang.Integer.min

class AdventDay05 : AdventDay() {
    override fun run() {
        val fileText = getFileAsText("day05")
        var furthestPoint = 0

        val ventMovements = fileText.split("\n").toTypedArray()
        val formattedVentMovements = ventMovements.map { movement ->
            val ventStartEnd = movement.split(" -> ")

            if (ventStartEnd.size < 2) {
                throw Exception("Not enough coordinates to calculate vent movement")
            }

            val start = Coordinates.fromString(ventStartEnd[0])
            val end = Coordinates.fromString(ventStartEnd[1])

            furthestPoint = listOf(furthestPoint, start.x, start.y, end.x, end.y).maxOrNull() ?: 0

            Pair(start, end)
        }

        val diagram: Array<IntArray> = Array(furthestPoint + 1) { IntArray(furthestPoint + 1) { 0 } }

        formattedVentMovements.forEach { movement ->
            val start = movement.first
            val end = movement.second

            val startingX = min(start.x, end.x)
            val endingX = max(start.x, end.x)
            val startingY = min(start.y, end.y)
            val endingY = max(start.y, end.y)

            when (start.calculateDirection(end)) {
                Direction.VERTICAL -> {
                    (startingY..endingY).forEach { y ->
                        diagram[start.x][y]++
                    }
                }
                Direction.HORIZONTAL -> {
                    (startingX..endingX).forEach { x ->
                        diagram[x][start.y]++
                    }
                }
                Direction.DIAGONAL -> {
                    val diagonalLength = endingY - startingY
                    val direction = start.calculateDiagonalDirection(end)
                    (0..diagonalLength).forEach { i ->
                        val x = start.x + (i * direction.first)
                        val y = start.y + (i * direction.second)
                        diagram[x][y]++
                    }
                }
                else -> {}
            }
        }

        val dangerousAreas = diagram.sumOf { i ->
            i.count {
                j ->
                j >= 2
            }
        }

        println("Dangerous areas: $dangerousAreas")
    }
}
