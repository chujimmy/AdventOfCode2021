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
                    val currentPoint = Point(x, y, pointHeight)
                    val isLowPoint = adjacentPointsOffsets
                        .map { o -> isPointLower(currentPoint, x + o.first, y + o.second) }
                        .reduce { acc, element -> acc && element }

                    Pair(Point(x, y, pointHeight), isLowPoint)
                }
            }
            .filter { it.second }
            .map { it.first }

        println("Risk Level: ${lowPoints.sumOf { it.height + 1 }}")

        val basins = lowPoints.associateWith { lowPoint ->
            val pointsToVisit: MutableList<Point> = mutableListOf(lowPoint)
            val basin: MutableSet<Point> = mutableSetOf()

            while (pointsToVisit.isNotEmpty()) {
                val point = pointsToVisit.removeFirst()
                basin.add(point)

                adjacentPointsOffsets.forEach loop@{ offset ->
                    val adjacentX = point.x + offset.first
                    val adjacentY = point.y + offset.second

                    if (isValidPoint(adjacentX, adjacentY)) {
                        val adjacentHeight = heightMap[adjacentY][adjacentX]
                        val adjacentPoint = Point(adjacentX, adjacentY, adjacentHeight)

                        if (adjacentHeight > point.height && adjacentHeight < 9 && !basin.contains(adjacentPoint)) {
                            pointsToVisit.add(adjacentPoint)
                        }
                    }
                }
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
