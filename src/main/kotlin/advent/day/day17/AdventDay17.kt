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
        .map {
            val startEnd = it.split("..")
            Pair(Integer.parseInt(startEnd[0]), Integer.parseInt(startEnd[1]))
        }

    override fun run() {
        val xTarget = targetArea[0]
        val yTarget = targetArea[1]
        val yMaxVelocity = abs(yTarget.first) - 1

        val velocityReach = mutableListOf(0)

        var summedVelocity = 0
        (1..yMaxVelocity).forEach { i ->
            summedVelocity += i
            velocityReach.add(summedVelocity)
        }

        val xMinVelocity = velocityReach.indexOfFirst { it in xTarget.first..xTarget.second }
        println("Velocity required for peak Y: ($xMinVelocity, $yMaxVelocity) with peak at ${velocityReach[yMaxVelocity]}")

        val possibleVelocities = (xMinVelocity..xTarget.second).flatMap { xVelocity ->
            (yTarget.first..yMaxVelocity).map { yVelocity ->
                if (canReachTarget(xTarget, yTarget, xVelocity, yVelocity)) {
                    Pair(xVelocity, yVelocity)
                } else {
                    null
                }
            }
        }.filterNotNull()

        println("Number of velocity settings to reach target: ${possibleVelocities.size}")
    }

    private fun canReachTarget(xTarget: Pair<Int, Int>, yTarget: Pair<Int, Int>, xVelocity: Int, yVelocity: Int): Boolean {
        var position = Pair(0, 0)
        var step = 0
        var isTargetReachable = true

        while (isTargetReachable) {
//            println("CanReachTarget $xVelocity $yVelocity Step $step")
            val currentXVelocity = max(xVelocity - step, 0)
            val currentYVelocity = yVelocity - step

            position = Pair(position.first + currentXVelocity, position.second + currentYVelocity)

            // No X velocity, so the probe will never reach the area, x wise
            if (currentXVelocity == 0 && position.first < xTarget.first) {
                isTargetReachable = false
            }

            // Probe has passed the area, on the X axis
            if (position.first > xTarget.second) {
                isTargetReachable = false
            }

            // Probe has passed below the area, on the Y axis
            if (position.second < yTarget.first) {
                isTargetReachable = false
            }

            if (isWithinTarget(xTarget, yTarget, position)) {
                return true
            }
            step++
        }

        return isTargetReachable
    }

    private fun isWithinTarget(xTarget: Pair<Int, Int>, yTarget: Pair<Int, Int>, position: Pair<Int, Int>): Boolean {
        val x = position.first
        val y = position.second

        return x >= xTarget.first && x <= xTarget.second && y >= yTarget.first && y <= yTarget.second
    }
}
