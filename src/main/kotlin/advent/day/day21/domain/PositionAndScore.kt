package advent.day.day21.domain

class PositionAndScore(val position: Int, val score: Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PositionAndScore

        if (position != other.position) return false
        if (score != other.score) return false

        return true
    }

    override fun hashCode(): Int {
        var result = position
        result = 31 * result + score
        return result
    }

    override fun toString(): String {
        return "Position=$position, Score=$score"
    }
}
