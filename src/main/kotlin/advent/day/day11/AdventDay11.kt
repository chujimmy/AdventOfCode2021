package advent.day.day11

import advent.AdventDay

class AdventDay11 : AdventDay() {
    override fun run() {
        val fileText = getFileAsText("day11")

        var octopusesMap: MutableList<MutableList<Int>> = fileText
            .split("\n")
            .map { line ->
                line.toCharArray()
                    .map { char -> char.digitToIntOrNull() }
                    .filterNotNull()
                    .toMutableList()
            }.toMutableList()

        var totalFlashes = 0
        (1..100).forEach { step ->
            octopusesMap = octopusesMap.mapIndexed { x, line ->
                line.mapIndexed { y, energyLevel ->
                    val countSurroundingFlashes = getAdjacentPoints(octopusesMap, Pair(x, y))
                        .map { getEnergyLevel(octopusesMap, it) }
                        .count { it == 9 }

                    // Check for octopuses surrounded by flashing octopuses
                    if (countSurroundingFlashes == 8) 10 else energyLevel + 1
                }.toMutableList()
            }.toMutableList()

            val handledFlashingOctopuses = mutableSetOf<Pair<Int, Int>>()
            var remainingFlashingOctopuses = getUnhandledFlashingOctopuses(octopusesMap, handledFlashingOctopuses)
            while (remainingFlashingOctopuses.isNotEmpty()) {
                val position = remainingFlashingOctopuses.removeFirst()

                getAdjacentPoints(octopusesMap, position).forEach { octopusesMap[it.first][it.second] += 1 }
                handledFlashingOctopuses.add(position)

                remainingFlashingOctopuses = getUnhandledFlashingOctopuses(octopusesMap, handledFlashingOctopuses)
            }

            octopusesMap = octopusesMap.mapIndexed { _, line ->
                line.mapIndexed { _, energyLevel ->
                    if (energyLevel >= 10) {
                        totalFlashes++
                        0
                    } else {
                        energyLevel
                    }
                }.toMutableList()
            }.toMutableList()

            printMap(step, octopusesMap)
        }

        println("Total flashes: $totalFlashes")
    }

    private fun getUnhandledFlashingOctopuses(
        octopusesMap: List<List<Int>>,
        handledOctopusesFlash: Set<Pair<Int, Int>> = emptySet()
    ): MutableList<Pair<Int, Int>> {
        return octopusesMap
            .flatMapIndexed { x, line ->
                line.mapIndexed { y, energyLevel ->
                    if (energyLevel > 9) {
                        Pair(x, y)
                    } else {
                        null
                    }
                }
            }
            .filterNotNull()
            .filter { ! handledOctopusesFlash.contains(it) }
            .toMutableList()
    }

    private fun printMap(step: Int, octopusesMap: List<List<Int>>) {
        val mapStr = octopusesMap.joinToString("") {
            it.joinToString("") + "\n"
        }
        println("Map, Step $step \n$mapStr")
    }

    private fun getAdjacentPoints(octopusesMap: List<List<Int>>, position: Pair<Int, Int>): List<Pair<Int, Int>> {
        return (-1..1)
            .flatMap {
                x ->
                (-1..1).map { y ->
                    if (x != 0 || y != 0) {
                        Pair(position.first + x, position.second + y)
                    } else {
                        null
                    }
                }
            }
            .filterNotNull()
            .filter { getEnergyLevel(octopusesMap, it) >= 0 }
    }

    private fun getEnergyLevel(octopusesMap: List<List<Int>>, position: Pair<Int, Int>): Int {
        val x = position.first
        val y = position.second

        if (x < 0 || x >= octopusesMap.size || y < 0 || y >= octopusesMap[0].size) {
            return -1
        }

        return octopusesMap[x][y]
    }
}
