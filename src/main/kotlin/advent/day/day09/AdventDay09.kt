package advent.day.day09

import advent.AdventDay

class AdventDay09 : AdventDay() {
    override fun run() {
        val fileText = getFileAsText("day09")

        val heightMap: Array<Array<Int>> = fileText.split("\n")
            .map { line ->
                line.toCharArray()
                    .map { it.digitToInt() }
                    .toTypedArray()
            }
            .toTypedArray()

        val lowPoints: MutableList<Pair<Int, Int>> = mutableListOf()
        var countLowPoints = 0
        var sumLowPoints = 0

        heightMap.forEachIndexed { x, line ->
            line.forEachIndexed { y, height ->
                val topHeight = getHeight(heightMap, x - 1, y)
                val bottomHeight = getHeight(heightMap, x + 1, y)
                val leftHeight = getHeight(heightMap, x, y - 1)
                val rightHeight = getHeight(heightMap, x, y + 1)

                if ((height in 0 until topHeight || topHeight == -1) &&
                    (height in 0 until bottomHeight || bottomHeight == -1) &&
                    (height in 0 until leftHeight || leftHeight == -1) &&
                    (height in 0 until rightHeight || rightHeight == -1)
                ) {
                    lowPoints.add(Pair(x, y))
                    countLowPoints += 1
                    sumLowPoints += (height + 1)
                }
            }
        }

        val basins: MutableMap<Pair<Int, Int>, MutableSet<Pair<Int, Int>>> = mutableMapOf()

        val offsetsToConsider: Array<Pair<Int, Int>> = arrayOf(
            Pair(-1, 0),
            Pair(1, 0),
            Pair(0, -1),
            Pair(0, 1),
        )

        lowPoints.forEach { lowPoint ->
            val pointsToVisit: MutableList<Pair<Int, Int>> = mutableListOf()
            pointsToVisit.add(lowPoint)

            while (pointsToVisit.isNotEmpty()) {
                val point = pointsToVisit.removeFirst()
                val pointHeight = getHeight(heightMap, point)

                val basin = basins.getOrDefault(lowPoint, mutableSetOf())
                basin.add(point)
                basins[lowPoint] = basin

                offsetsToConsider.forEach loop@{ offset ->
                    val adjacentX = point.first + offset.first
                    val adjacentY = point.second + offset.second

                    val adjacentPoint = Pair(adjacentX, adjacentY)
                    val adjacentHeight = getHeight(heightMap, adjacentPoint)

                    if (adjacentHeight > 0 &&
                        adjacentHeight > pointHeight &&
                        adjacentHeight < 9 &&
                        !basin.contains(adjacentPoint)
                    ) {
                        pointsToVisit.add(adjacentPoint)
                    }
                }
            }
        }

        val multiplication = basins.values
            .sortedByDescending { it.size }
            .take(3)
            .map { it.size }
            .reduce { acc, next -> acc * next }

        println("3 biggest basins multiplication: $multiplication")
    }

    private fun getHeight(heightMap: Array<Array<Int>>, x: Int, y: Int): Int {
        if (x < 0 || x >= heightMap.size) {
            return -1
        }

        if (y < 0 || y >= heightMap[0].size) {
            return -1
        }

        return heightMap[x][y]
    }

    private fun getHeight(heightMap: Array<Array<Int>>, pair: Pair<Int, Int>): Int {
        return getHeight(heightMap, pair.first, pair.second)
    }
}
