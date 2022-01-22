package advent.day.day19.domain

import advent.day.day19.domain.Axis.*
import kotlin.math.pow

class Coordinates(val x: Double, val y: Double, val z: Double) {
    fun getDistance(other: Coordinates): Double {
        val sum = (x - other.x).pow(2) + (y - other.y).pow(2) + (z - other.z).pow(2)

        return kotlin.math.sqrt(sum)
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Coordinates

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        return result
    }

    override fun toString(): String {
        return "(${x.toInt()},${y.toInt()},${z.toInt()})"
    }

    companion object {
        fun fromString(coordinatesStr: String): Coordinates {
            val coordinates = coordinatesStr.split(",")
            return Coordinates(coordinates[0].toDouble(), coordinates[1].toDouble(), coordinates[2].toDouble())
        }
    }
}
