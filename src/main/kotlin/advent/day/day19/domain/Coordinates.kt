package advent.day.day19.domain

import advent.day.day19.domain.Axis.X
import advent.day.day19.domain.Axis.Y
import advent.day.day19.domain.Axis.Z
import kotlin.math.abs
import kotlin.math.pow

data class Coordinates(val x: Double, val y: Double, val z: Double) {
    fun getDistance(other: Coordinates): Double {
        val sum = (x - other.x).pow(2) + (y - other.y).pow(2) + (z - other.z).pow(2)

        return kotlin.math.sqrt(sum)
    }

    fun getManhattanDistance(other: Coordinates): Double {
        return abs(x - other.x) + abs(y - other.y) + abs(z - other.z)
    }

    fun translateCoordinates(rotation: Rotation): Coordinates {
        val xAxisValue = translateAxis(rotation.xAxis)
        val xOrientation = rotation.xOrientation.orientation
        val yAxisValue = translateAxis(rotation.yAxis)
        val yOrientation = rotation.yOrientation.orientation
        val zAxisValue = translateAxis(rotation.zAxis)
        val zOrientation = rotation.zOrientation.orientation

        return Coordinates(xAxisValue * xOrientation, yAxisValue * yOrientation, zAxisValue * zOrientation)
    }

    private fun translateAxis(axis: Axis): Double {
        return when (axis) {
            X -> x
            Y -> y
            Z -> z
        }
    }

    operator fun plus(other: Coordinates): Coordinates {
        return Coordinates(x + other.x, y + other.y, z + other.z)
    }

    operator fun minus(other: Coordinates): Coordinates {
        return Coordinates(x - other.x, y - other.y, z - other.z)
    }

    companion object {
        fun fromString(coordinatesStr: String): Coordinates {
            val coordinates = coordinatesStr.split(",")
            return Coordinates(coordinates[0].toDouble(), coordinates[1].toDouble(), coordinates[2].toDouble())
        }
    }
}
