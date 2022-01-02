package advent.day.day17

import advent.AdventDay
import java.lang.Integer.max
import kotlin.math.abs

class AdventDay17 : AdventDay() {
    private val fileContent = getFileAsText("day17")

    private val targetArea: List<Pair<Int, Int>> = fileContent
        .removePrefix("target area: ")
        .split(",")
        .map { it.trim().substring(2) }
        .map { it.split("..") }
        .map { startEnd -> Pair(Integer.parseInt(startEnd[0]), Integer.parseInt(startEnd[1])) }

    override fun run() {
        val xTarget = targetArea[0]
        val yTarget = targetArea[1]
        val yMaxVelocity = abs(yTarget.first) - 1

        val velocityReach = (0..yMaxVelocity).map { i -> (i downTo 0).sumOf { it } }
        val xMinVelocity = velocityReach.indexOfFirst { it in xTarget.first..xTarget.second }

        println("Velocity required for peak Y: ($xMinVelocity, $yMaxVelocity) with peak at ${velocityReach[yMaxVelocity]}")

        val possibleVelocities = (xMinVelocity..xTarget.second).sumOf { xVelocity ->
            (yTarget.first..yMaxVelocity).count { yVelocity ->
                canReachTarget(xTarget, yTarget, xVelocity, yVelocity)
            }
        }

        println("Number of velocity settings to reach target: $possibleVelocities")
    }

    private fun canReachTarget(xTarget: Pair<Int, Int>, yTarget: Pair<Int, Int>, xVelocity: Int, yVelocity: Int): Boolean {
        var position = Pair(0, 0)
        var step = 0

        while (true) {
            val currentXVelocity = max(xVelocity - step, 0)
            val currentYVelocity = yVelocity - step

            position = Pair(position.first + currentXVelocity, position.second + currentYVelocity)

            // If the X velocity is equal to 0, without reaching the target
            // Or the probe has passed the area on the X axis
            // Or the probe is below the area on the Y axis
            if (currentXVelocity == 0 && position.first < xTarget.first ||
                position.first > xTarget.second ||
                position.second < yTarget.first
            ) {
                return false
            }

            if (isWithinTarget(xTarget, yTarget, position)) {
                return true
            }
            step++
        }
    }

    private fun isWithinTarget(xTarget: Pair<Int, Int>, yTarget: Pair<Int, Int>, position: Pair<Int, Int>): Boolean {
        val x = position.first
        val y = position.second

        return x >= xTarget.first && x <= xTarget.second && y >= yTarget.first && y <= yTarget.second
    }
}
