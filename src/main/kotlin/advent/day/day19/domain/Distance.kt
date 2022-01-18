package advent.day.day19.domain

import kotlin.math.pow

class Distance(private val a: Coordinates, private val b: Coordinates) {
    fun getDistance(): Double {
        val sum = (a.x - b.x).pow(2) + (a.y - b.y).pow(2) + (a.z - b.z).pow(2)

        return kotlin.math.sqrt(sum)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Distance

        return getDistance().equals(other.getDistance())
    }

    override fun hashCode(): Int {
        return getDistance().hashCode()
    }


}