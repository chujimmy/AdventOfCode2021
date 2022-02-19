package advent.day.day17

import advent.AdventDay
import advent.day.day17.domain.Position
import java.lang.Integer.max
import kotlin.math.abs

class AdventDay17 : AdventDay() {
    private val fileContent = getFileAsText("day17")

    private val targetArea = fileContent
        .removePrefix("target area: ")
        .split(",")
        .map { it.trim().substring(2) }
        .map { it.split("..") }
        .map { startEnd -> IntRange(Integer.parseInt(startEnd[0]), Integer.parseInt(startEnd[1])) }
    private val xTarget = targetArea[0]
    private val yTarget = targetArea[1]

    override fun run() {
        val yMaxVelocity = abs(yTarget.first) - 1
        val velocityReach = (0..yMaxVelocity).map { i -> (i downTo 0).sumOf { it } }
        val xMinVelocity = velocityReach.indexOfFirst { it in xTarget.first..xTarget.last }

        println("Peak Y: ${velocityReach[yMaxVelocity]}. Velocity required : ($xMinVelocity, $yMaxVelocity)")

        val possibleVelocities = (xMinVelocity..xTarget.last).sumOf { xVelocity ->
            (yTarget.first..yMaxVelocity).count { yVelocity ->
                canReachTarget(xTarget, yTarget, xVelocity, yVelocity)
            }
        }

        println("Number of velocity settings to reach target: $possibleVelocities")
    }

    private fun canReachTarget(xTarget: IntRange, yTarget: IntRange, xVelocity: Int, yVelocity: Int): Boolean {
        var position = Position(0, 0)
        var step = 0

        while (true) {
            val currentXVelocity = max(xVelocity - step, 0)
            val currentYVelocity = yVelocity - step

            position = Position(position.x + currentXVelocity, position.y + currentYVelocity)

            // If the X velocity is equal to 0, without reaching the target
            // Or the probe has passed the area on the X axis
            // Or the probe is below the area on the Y axis
            if (currentXVelocity == 0 && position.x < xTarget.first ||
                position.x > xTarget.last ||
                position.y < yTarget.first
            ) {
                return false
            }

            if (isWithinTarget(xTarget, yTarget, position)) {
                return true
            }
            step++
        }
    }

    private fun isWithinTarget(xTarget: IntRange, yTarget: IntRange, position: Position): Boolean {
        return position.x in xTarget && position.y in yTarget
    }
}
