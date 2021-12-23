package advent.day.day07

import advent.AdventDay
import kotlin.math.abs

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
        val fuelConsumptions: MutableMap<Int, Int> = mutableMapOf()
        var summedFuelConsumption = 0
        (0..furthestCrabPosition).forEach { position ->
            summedFuelConsumption += position
            fuelConsumptions[position] = summedFuelConsumption
        }

        val minFuel = calculateOptimalCrabsPosition(fuelConsumptions)
        println("Min Fuel Part 2: $minFuel")
    }

    private fun calculateOptimalCrabsPosition(fuelConsumptions: Map<Int, Int>): Long {
        val optimalCrabPositions: MutableMap<Int, Long> = mutableMapOf()

        (0..furthestCrabPosition).forEach { position ->
            val totalFuelRequired = crabPositions
                .map { it }
                .fold(0L) { acc, pair ->
                    val distance = abs(pair.key - position)

                    acc + (pair.value * fuelConsumptions.getOrDefault(distance, 0))
                }

            optimalCrabPositions[position] = totalFuelRequired
        }

        return optimalCrabPositions.values.minOrNull()!!
    }
}
