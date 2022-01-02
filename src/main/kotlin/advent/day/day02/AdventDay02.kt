package advent.day.day02

import advent.AdventDay
import advent.day.day02.domain.Movement

class AdventDay02 : AdventDay() {
    private val fileText = getFileAsText("day02")
    private val movements = fileText
        .split("\n")
        .map { it.split(" ") }
        .map { Pair(Movement.fromName(it[0]), it[1].toInt()) }

    override fun run() {
        runPart01()
        runPart02()
    }

    private fun runPart01() {
        var hPosition = 0
        var depth = 0

        movements.forEach { movement ->
            val movementUnits = movement.second
            val movementMultiplier = movement.first.multiplier

            when (movement.first) {
                Movement.UP, Movement.DOWN -> { depth += movementUnits * movementMultiplier }
                Movement.FORWARD -> { hPosition += movementUnits }
                else -> {}
            }
        }

        println("Result: ${hPosition * depth}")
    }

    private fun runPart02() {
        var depth = 0
        var hPosition = 0
        var aim = 0

        movements.forEach { movement ->
            val movementUnits = movement.second
            val movementMultiplier = movement.first.multiplier

            when (movement.first) {
                Movement.UP, Movement.DOWN -> {
                    aim += movementUnits * movementMultiplier
                }
                Movement.FORWARD -> {
                    hPosition += movementUnits
                    depth += aim * movementUnits
                }
                else -> {}
            }
        }

        println("Result: ${depth * hPosition}")
    }
}
