package advent.day.day07

import advent.AdventDay
import kotlin.math.abs

class AdventDay07 : AdventDay() {
    override fun run() {
        val fileText = getFileAsText("day07")
        val optimalCrabPositions: MutableMap<Int, Int> = mutableMapOf()
        val fuelConsumptions: MutableMap<Int, Int> = mutableMapOf()

        val crabPositions = fileText.split(",")
            .map { it.trim().toInt() }
            .groupBy { it }
            .mapValues { it.value.size }

        val maxCrabPosition = crabPositions.maxOf { it.key }

        var fuelConsumption = 0
        (0..maxCrabPosition).forEach { distance ->
            fuelConsumption += distance
            fuelConsumptions[distance] = fuelConsumption
        }

        (0..maxCrabPosition).forEach { position ->
            var totalFuelRequired = 0

            crabPositions.forEach { (crabPosition, crabCount) ->
                val distance = abs(crabPosition - position)
                totalFuelRequired += fuelConsumptions.getOrDefault(distance, 0) * crabCount
            }

            optimalCrabPositions[position] = totalFuelRequired
        }

        var minFuel = Int.MAX_VALUE
        optimalCrabPositions.forEach { (_, fuel) ->
            if (fuel < minFuel) {
                minFuel = fuel
            }
        }

        println("Min fuel $minFuel")
    }
}
