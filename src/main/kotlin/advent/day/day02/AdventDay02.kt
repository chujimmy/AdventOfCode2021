package advent.day.day02

import advent.AdventDay
import advent.day.day02.domain.Movement
import advent.day.day02.domain.Movement.DOWN
import advent.day.day02.domain.Movement.FORWARD
import advent.day.day02.domain.Movement.UP
import advent.day.day02.domain.Position

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
        val position = movements.fold(Position()) { acc, movement ->
            val units = movement.second
            val multiplier = movement.first.multiplier

            when (movement.first) {
                UP, DOWN -> { Position(acc.hPosition, acc.depth + units * multiplier) }
                FORWARD -> { Position(acc.hPosition + units, acc.depth) }
            }
        }

        println("Result: ${position.hPosition * position.depth}")
    }

    private fun runPart02() {
        val position = movements.fold(Position()) { p, movement ->
            val units = movement.second
            val multiplier = movement.first.multiplier

            when (movement.first) {
                UP, DOWN -> { Position(p.hPosition, p.depth, p.aim + (units * multiplier)) }
                FORWARD -> { Position(p.hPosition + units, p.depth + (p.aim * units), p.aim) }
            }
        }

        println("Result: ${position.depth * position.hPosition}")
    }
}
