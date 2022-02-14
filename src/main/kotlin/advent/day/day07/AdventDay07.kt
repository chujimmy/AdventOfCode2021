package advent.day.day07

import advent.AdventDay
import kotlin.math.abs
import kotlin.math.min

class AdventDay07 : AdventDay() {
    private val fileText = getFileAsText("day07")
    private val crabPositions = fileText.split(",")
        .map { it.trim().toInt() }
        .groupingBy { it }
        .eachCount()
    private val furthestCrabPosition = crabPositions.maxOf { it.key }

    override fun run() {
        runPart01()
        runPart02()
    }

    private fun runPart01() {
        val fuelConsumptions = (0..furthestCrabPosition)
            .map { it }
            .associateWith { it }

        val minFuel = calculateOptimalCrabsPosition(fuelConsumptions)
        println("Min Fuel Part 1: $minFuel")
    }

    private fun runPart02() {
        val map = mapOf<Int, Int>()
        val fuelConsumptions = (0..furthestCrabPosition)
            .fold(map) { acc, position -> acc + mapOf(position to ((acc.maxOfOrNull { it.value } ?: 0) + position)) }

        val minFuel = calculateOptimalCrabsPosition(fuelConsumptions)
        println("Min Fuel Part 2: $minFuel")
    }

    private fun calculateOptimalCrabsPosition(fuelConsumptions: Map<Int, Int>): Long {
        return (0..furthestCrabPosition)
            .fold(Long.MAX_VALUE) { currentMinEnergy, position ->
                val totalFuelRequired = crabPositions
                    .entries
                    .fold(0L) { acc, pair ->
                        val distance = abs(pair.key - position)
                        acc + (pair.value * fuelConsumptions.getValue(distance))
                    }

                min(currentMinEnergy, totalFuelRequired)
            }
    }
}
