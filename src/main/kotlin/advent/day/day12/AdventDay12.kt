package advent.day.day12

import advent.AdventDay

class AdventDay12 : AdventDay() {
    private val fileText = getFileAsText("day12")

    private val paths = fileText
        .split("\n")
        .map { it.split("-") }
        .flatMap { listOf(it[1] to it[0], it[0] to it[1]) }
        .groupBy { it.first }
        .mapValues { (_, values) -> values.map { it.second } }

    override fun run() {
        val start = "start"
        val smallCaveSingleVisitPathCount = countPathsOneVisitSmallCave(start, start)
        println("Number of paths visiting small caves only once: ${smallCaveSingleVisitPathCount.size}")

        val smallCaveOneDoubleVisitPathCount = countPathsTwoVisitsOneSmallCave(start, start)
        println("Number of paths with one double small caves visit: ${smallCaveOneDoubleVisitPathCount.size}")
    }

    private fun countPathsOneVisitSmallCave(path: String, currentCave: String): Set<String> {
        val validPaths = mutableSetOf<String>()
        if (currentCave == "end") {
            return setOf(path)
        }

        this.paths.getValue(currentCave).forEach { nextCave ->
            if (nextCave.uppercase() == nextCave || nextCave !in path) {
                validPaths.addAll(countPathsOneVisitSmallCave("$path,$nextCave", nextCave))
            }
        }

        return validPaths
    }

    private fun countPathsTwoVisitsOneSmallCave(path: String, currentCave: String): Set<String> {
        val validPaths = mutableSetOf<String>()
        if (currentCave == "end") {
            return setOf(path)
        }

        if (currentCave == "start" && path != "start") {
            return emptySet()
        }

        this.paths.getValue(currentCave).forEach { nextCave ->
            val updatedPath = "$path,$nextCave"

            if (nextCave.uppercase() == nextCave) {
                validPaths.addAll(countPathsTwoVisitsOneSmallCave(updatedPath, nextCave))
            } else {
                val smallCavesVisits = path
                    .split(",")
                    .filter { it.lowercase() == it && it !in arrayOf("start", "end") }
                    .groupingBy { it }
                    .eachCount()
                val secondVisit = smallCavesVisits.count { it.value == 2 } == 1

                when (smallCavesVisits[nextCave]) {
                    0, null -> validPaths.addAll(countPathsTwoVisitsOneSmallCave(updatedPath, nextCave))
                    1 -> { if (!secondVisit) validPaths.addAll(countPathsTwoVisitsOneSmallCave(updatedPath, nextCave)) }
                    else -> {}
                }
            }
        }

        return validPaths
    }
}
