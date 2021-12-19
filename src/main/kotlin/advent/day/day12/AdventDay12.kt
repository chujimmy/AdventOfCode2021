package advent.day.day12

import advent.AdventDay

class AdventDay12 : AdventDay() {

    private val paths: MutableMap<String, MutableSet<String>> = hashMapOf()

    override fun run() {
        getFileAsText("day12")
            .split("\n")
            .forEach {
                val path = it.split("-")
                addPath(path[0], path[1])
                addPath(path[1], path[0])
            }

        val smallCaveSingleVisitPathCount = countPathsSmallCaveSingleVisit(emptySet(), "start", "start")
        val smallCaveOneDoubleVisitPathCount = countPathsOneSmallCaveDoubleVisit(emptySet(), "start", "start")

        println("Number of paths visiting small caves only once: ${smallCaveSingleVisitPathCount.size}")
        println("Number of paths with one double small caves visit: ${smallCaveOneDoubleVisitPathCount.size}")
    }

    private fun countPathsSmallCaveSingleVisit(paths: Set<String>, currentPath: String, currentCave: String): Set<String> {
        val validPaths: MutableSet<String> = mutableSetOf()
        if (currentCave == "end") {
            return setOf(currentPath)
        }

        this.paths[currentCave]?.forEach { nextCave ->
            val updatedPath = "$currentPath,$nextCave"

            if (nextCave.uppercase() == nextCave || !currentPath.contains(nextCave)) {
                validPaths.addAll(countPathsSmallCaveSingleVisit(paths, updatedPath, nextCave))
            }
        }

        return validPaths
    }

    private fun countPathsOneSmallCaveDoubleVisit(paths: Set<String>, currentPath: String, currentCave: String): Set<String> {
        val validPaths: MutableSet<String> = mutableSetOf()
        if (currentCave == "end") {
            return setOf(currentPath)
        }

        if (currentCave == "start" && currentPath != "start") {
            return emptySet()
        }

        this.paths[currentCave]?.forEach { nextCave ->
            val updatedPath = "$currentPath,$nextCave"

            if (nextCave.uppercase() == nextCave) {
                validPaths.addAll(countPathsOneSmallCaveDoubleVisit(paths, updatedPath, nextCave))
            } else {
                val smallCavesVisits = currentPath
                    .split(",")
                    .filter { it.lowercase() == it && it !in arrayOf("start", "end") }
                    .groupingBy { it }
                    .eachCount()
                val doubleVisit = smallCavesVisits.count { it.value == 2 } == 1

                when (smallCavesVisits[nextCave]) {
                    0, null -> validPaths.addAll(countPathsOneSmallCaveDoubleVisit(paths, updatedPath, nextCave))
                    1 -> {
                        if (!doubleVisit) {
                            validPaths.addAll(countPathsOneSmallCaveDoubleVisit(paths, updatedPath, nextCave))
                        }
                    }
                    else -> {}
                }
            }
        }

        return validPaths
    }

    private fun addPath(start: String, finish: String) {
        val paths = this.paths.getOrDefault(start, mutableSetOf())
        paths.add(finish)

        this.paths[start] = paths
    }
}
