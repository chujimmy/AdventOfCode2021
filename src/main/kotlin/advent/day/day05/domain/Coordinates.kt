package advent.day.day05.domain

import kotlin.math.abs

class Coordinates(val x: Int, val y: Int) {
    fun calculateDirection(end: Coordinates): Direction? {
        if (this == end) {
            return Direction.NONE
        }

        return when {
            this.x == end.x -> Direction.VERTICAL
            this.y == end.y -> Direction.HORIZONTAL
            abs(this.x - end.x) == abs(this.y - end.y) -> Direction.DIAGONAL
            else -> null
        }
    }

    fun calculateDiagonalDirection(end: Coordinates): Pair<Int, Int> {
        val xMovement = when {
            this.x > end.x -> -1
            this.x < end.x -> 1
            else -> 0
        }

        val yMovement = when {
            this.y > end.y -> -1
            this.y < end.y -> 1
            else -> 0
        }

        return Pair(xMovement, yMovement)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Coordinates

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }

    companion object {
        fun fromString(coordinatesStr: String): Coordinates {
            val coordinates = coordinatesStr.split(",")
            return Coordinates(coordinates[0].toInt(), coordinates[1].toInt())
        }
    }
}
