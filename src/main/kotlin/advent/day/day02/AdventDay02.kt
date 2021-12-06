package advent.day.day02

import advent.AdventDay
import advent.day.day02.domain.Movement

class AdventDay02: AdventDay() {
    override fun run() {
        val fileText = getFileAsText("day02")
        val movements: Array<String> = fileText.split("\n").toTypedArray()

        var depth = 0
        var position = 0
        var aim = 0

        for (movementValue in movements) {
            val pair = movementValue.split(" ").let{
                Pair(Movement.fromMovementName(it[0]), it[1].toInt())
            }

            val movement = pair.first
            val units = pair.second
            when (movement) {
                Movement.UP, Movement.DOWN -> {
                    aim += units * movement.multiplier
                }
                Movement.FORWARD -> {
                    position += units * movement.multiplier
                    depth += units * aim
                }
                else -> {}
            }

        }

        println("Result: ${position * depth}")
    }
}