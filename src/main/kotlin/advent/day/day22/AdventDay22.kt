package advent.day.day22

import advent.AdventDay
import advent.day.day22.domain.Cube
import advent.day.day22.domain.Cuboid

class AdventDay22 : AdventDay() {
    private val fileText = getFileAsText("day22")
    private val cubesInstructions = fileText
        .split("\n")
        .map { it.split(" ") }
        .map { Pair(it[0], it[1]) }

    override fun run() {
        runPart01()
        runPart02()
    }

    private fun runPart01() {
        val turnedOnCubes = cubesInstructions
            .map { Pair(it.first, Cuboid.fromString(it.second, it.first, true)) }
            .map { Pair(it.first, it.second.getCubes()) }
            .fold(mutableSetOf<Cube>()) { acc, pair ->
                if (pair.first == "on") acc.addAll(pair.second) else acc.removeAll(pair.second)
                acc
            }

        println("Turned on cubes: ${turnedOnCubes.size}")
    }

    private fun runPart02() {
        val cubes = mutableListOf<Cuboid>()

        cubesInstructions
            .map { Pair(it.first, Cuboid.fromString(it.second, it.first, false)) }
            .forEach { pair ->
                val instruction = pair.first
                val current = pair.second
                val cuboidIntersections = cubes.mapNotNull { c -> getOverlap(current, c) }

                cubes.addAll(cuboidIntersections)

                if (instruction == "on") {
                    cubes.add(current)
                }
            }

        val sum = cubes.sumOf { it.volume().times(it.state.toBigInteger()) }
        println("Turned on cubes: $sum")
    }

    private fun getOverlap(cube1: Cuboid, cube2: Cuboid): Cuboid? {
        val xOverlap = getAxisOverlap(cube1.xMin, cube1.xMax, cube2.xMin, cube2.xMax)
        val yOverlap = getAxisOverlap(cube1.yMin, cube1.yMax, cube2.yMin, cube2.yMax)
        val zOverlap = getAxisOverlap(cube1.zMin, cube1.zMax, cube2.zMin, cube2.zMax)

        var status = cube1.state * cube2.state
        if (cube1.state == cube2.state) {
            status = -cube1.state
        } else if (cube1.state == 1 && cube2.state == -1) {
            status = 1
        }

        if (xOverlap != null && yOverlap != null && zOverlap != null) {
            return Cuboid(
                xOverlap.first,
                xOverlap.second,
                yOverlap.first,
                yOverlap.second,
                zOverlap.first,
                zOverlap.second,
                status,
            )
        }

        return null
    }

    private fun getAxisOverlap(min1: Int, max1: Int, min2: Int, max2: Int): Pair<Int, Int>? {
        if (max2 < min1) {
            return getAxisOverlap(min2, max2, min1, max1)
        }

        if (min2 <= max1) {
            return Pair(Integer.max(min2, min1), Integer.min(max1, max2))
        }

        return null
    }
}
