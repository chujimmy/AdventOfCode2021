package advent.day.day11

import advent.AdventDay

class AdventDay11 : AdventDay() {
    private val fileText = getFileAsText("day11")
    private val initialOctopusMap = fileText
        .split("\n")
        .map { l -> l.toCharArray().map { c -> c.digitToInt() } }

    override fun run() {
        val finalMap = (1..100).fold(Pair(initialOctopusMap, 0)) { acc, step ->
            val map = acc.first
            val octopusMap = map.mapIndexed { y, hLine ->
                hLine.mapIndexed { x, energyLevel ->
                    val countSurroundingFlashes = getAdjacentPoints(map, Pair(x, y))
                        .map { getEnergyLevel(map, it) }
                        .count { energy -> energy == 9 }

                    if (countSurroundingFlashes == 8) 10 else energyLevel + 1
                }.toMutableList()
            }.toMutableList()

            val handledFlashingOctopuses = ArrayDeque<Pair<Int, Int>>()
            val flashingOctopusesToUpdate = getUnhandledFlashingOctopuses(octopusMap)

            while (flashingOctopusesToUpdate.isNotEmpty()) {
                val position = flashingOctopusesToUpdate.removeFirst()

                getAdjacentPoints(octopusMap, position).forEach { octopusMap[it.second][it.first] += 1 }
                handledFlashingOctopuses.add(position)

                flashingOctopusesToUpdate.addAll(
                    getUnhandledFlashingOctopuses(octopusMap)
                        .minus(handledFlashingOctopuses)
                        .minus(flashingOctopusesToUpdate)
                )
            }

            val updatedMap = octopusMap.mapIndexed { _, line ->
                line.mapIndexed { _, energyLevel ->
                    if (energyLevel >= 10) 0 else energyLevel
                }
            }

            printMap(step, updatedMap)

            Pair(updatedMap, acc.second + updatedMap.sumOf { hLine -> hLine.count { energy -> energy == 0 } })
        }

        println("Total flashes: ${finalMap.second}")
    }

    private fun getUnhandledFlashingOctopuses(octopusMap: List<List<Int>>): ArrayDeque<Pair<Int, Int>> {
        return ArrayDeque(
            octopusMap.flatMapIndexed { y, hLine ->
                hLine.mapIndexed { x, energyLevel ->
                    if (energyLevel > 9) Pair(x, y) else null
                }
            }.filterNotNull()
        )
    }

    private fun printMap(step: Int, map: List<List<Int>>) {
        val allZeros = map.all { hLine -> hLine.all { it == 0 } }
        if (allZeros) {
            println("All octopuses flash at the same time, Step $step")
        }
        val mapStr = map.joinToString("\n") { it.joinToString("") }
        println("Map, Step $step \n$mapStr\n")
    }

    private fun getAdjacentPoints(octopusMap: List<List<Int>>, position: Pair<Int, Int>): List<Pair<Int, Int>> {
        return (-1..1).flatMap { y ->
            (-1..1).map { x ->
                Pair(position.first + x, position.second + y)
            }
        }
            .filter { isPositionOnMap(octopusMap, it) }
            .filter { it != position }
    }

    private fun isPositionOnMap(octopusMap: List<List<Int>>, position: Pair<Int, Int>): Boolean {
        val x = position.first
        val y = position.second

        return x >= 0 && x < octopusMap[0].size && y >= 0 && y < octopusMap.size
    }

    private fun getEnergyLevel(octopusMap: List<List<Int>>, position: Pair<Int, Int>): Int {
        val x = position.first
        val y = position.second
        return if (isPositionOnMap(octopusMap, position)) octopusMap[y][x] else -1
    }
}
