package advent.day.day09

import advent.AdventDay
import advent.day.day09.domain.Point

class AdventDay09 : AdventDay() {
    private val fileText = getFileAsText("day09")

    private val heightMap = fileText
        .split("\n")
        .map { it.toCharArray().map { d -> d.digitToInt() } }

    private val adjacentPointsOffsets = listOf(
        Pair(-1, 0),
        Pair(1, 0),
        Pair(0, -1),
        Pair(0, 1),
    )

    override fun run() {
        val lowPoints = heightMap
            .flatMapIndexed { y, hLine ->
                hLine.mapIndexed { x, pointHeight ->
                    Point(x, y, pointHeight)
                }
            }
            .associateWith { p -> adjacentPointsOffsets.map { Pair(p.x + it.first, p.y + it.second) } }
            .filter { it.value.all { a -> isPointLower(it.key, a.first, a.second) } }
            .map { it.key }

        println("Risk Level: ${lowPoints.sumOf { it.height + 1 }}")

        val basins = lowPoints.associateWith { lowPoint ->
            val pointsToVisit = mutableListOf(lowPoint)
            val basin = mutableSetOf<Point>()

            while (pointsToVisit.isNotEmpty()) {
                val point = pointsToVisit.removeFirst()
                val higherAdjacentPoints = adjacentPointsOffsets
                    .map { Pair(point.x + it.first, point.y + it.second) }
                    .filter { isValidPoint(it.first, it.second) }
                    .map { Point(it.first, it.second, heightMap[it.second][it.first]) }
                    .filter { adjacentPoint -> adjacentPoint.height > point.height && adjacentPoint.height < 9 }

                basin.add(point)
                pointsToVisit.addAll(higherAdjacentPoints)
            }
            basin
        }

        val multiplication = basins.values
            .sortedByDescending { it.size }
            .take(3)
            .map { it.size }
            .reduce { acc, next -> acc * next }

        println("3 biggest basins multiplication: $multiplication")
    }

    private fun isValidPoint(xToCheck: Int, yToCheck: Int): Boolean {
        return xToCheck >= 0 && xToCheck < heightMap.size && yToCheck >= 0 && yToCheck < heightMap[0].size
    }

    private fun isPointLower(comparisonPoint: Point, xToCheck: Int, yToCheck: Int): Boolean {
        if (!isValidPoint(xToCheck, yToCheck)) {
            return true
        }

        return comparisonPoint.height < heightMap[yToCheck][xToCheck]
    }
}
