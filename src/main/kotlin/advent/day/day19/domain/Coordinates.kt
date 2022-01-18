package advent.day.day19.domain

import kotlin.math.pow

class Coordinates(val x: Double, val y: Double, val z: Double) {
    fun getPossibleRotations(): List<Coordinates> {
        return listOf(
            Coordinates(x, y, z),
            Coordinates(-y, x, z),
            Coordinates(-x, -y, z),
            Coordinates(y, -x, z),
            Coordinates(x, -y, -z),
            Coordinates(y, x, -z),
            Coordinates(-x, y, -z),
            Coordinates(-y, -x, -z),

            Coordinates(x, z, -y),
            Coordinates(-y, z, -x),
            Coordinates(-x, z, y),
            Coordinates(y, z, x),
            Coordinates(-y, -z, x),
            Coordinates(-x, -z, -y),
            Coordinates(y, -z, -x),
            Coordinates(x, -z, y),

            Coordinates(z, x, y),
            Coordinates(z, -y, x),
            Coordinates(z, -x, -y),
            Coordinates(z, y, -x),
            Coordinates(-z, -y, -x),
            Coordinates(-z, -x, y),
            Coordinates(-z, y, x),
            Coordinates(-z, -x, -y),
        )
    }

    fun getDistance(other: Coordinates): Double {
        val sum = (x - other.x).pow(2) + (y - other.y).pow(2) + (z - other.z).pow(2)

        return kotlin.math.sqrt(sum)
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
        return "Coordinates(x=$x, y=$y, z=$z)"
    }


}