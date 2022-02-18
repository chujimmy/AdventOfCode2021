package advent.day.day11

import advent.AdventDay

class AdventDay11 : AdventDay() {
    private val fileText = getFileAsText("day11")
    private val initialOctopusMap = fileText
        .split("\n")
        .map { l -> l.toCharArray().map { c -> c.digitToInt() } }
    private val xSize = initialOctopusMap[0].size
    private val ySize = initialOctopusMap.size

    override fun run() {
        val finalMap = (1..250).fold(Pair(initialOctopusMap, 0)) { acc, step ->
            val octopusMap = acc.first.mapIndexed { y, hLine ->
                hLine.mapIndexed { x, energyLevel ->
                    val countSurroundingFlashes = getAdjacentPoints(Pair(x, y))
                        .map { acc.first[it.second][it.first] }
                        .count { energy -> energy == 9 }

                    if (countSurroundingFlashes == 8) 10 else energyLevel + 1
                }.toMutableList()
            }.toMutableList()

            val handledFlashingOctopuses = ArrayDeque<Pair<Int, Int>>()
            val flashingOctopusesToUpdate = getUnhandledFlashingOctopuses(octopusMap)

            while (flashingOctopusesToUpdate.isNotEmpty()) {
                val position = flashingOctopusesToUpdate.removeFirst()

                getAdjacentPoints(position).forEach { octopusMap[it.second][it.first] += 1 }
                handledFlashingOctopuses.add(position)

                flashingOctopusesToUpdate.addAll(
                    getUnhandledFlashingOctopuses(octopusMap)
                        .minus(handledFlashingOctopuses)
                        .minus(flashingOctopusesToUpdate)
                )
            }

            (0 until ySize).forEach { y ->
                (0 until xSize).forEach { x ->
                    if (octopusMap[y][x] >= 10) octopusMap[y][x] = 0
                }
            }

            printMap(step, octopusMap)

            Pair(octopusMap, acc.second + octopusMap.sumOf { hLine -> hLine.count { energy -> energy == 0 } })
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
        val mapStr = map.joinToString("\n") { it.joinToString("") }
        print(if (allZeros) "All octopuses flash at the same time\n" else "")
        println("Map, Step $step \n$mapStr\n")
    }

    private fun getAdjacentPoints(position: Pair<Int, Int>): List<Pair<Int, Int>> {
        return (-1..1).flatMap { y ->
            (-1..1).map { x ->
                Pair(position.first + x, position.second + y)
            }
        }
            .filter { isPositionValid(it) }
            .filter { it != position }
    }

    private fun isPositionValid(position: Pair<Int, Int>): Boolean {
        val x = position.first
        val y = position.second

        return x in 0 until xSize && y in 0 until ySize
    }
}
