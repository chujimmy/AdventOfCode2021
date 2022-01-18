package advent.day.day19.domain

class Rotation(val axis: Axis, val rotation: Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rotation

        if (axis != other.axis) return false
        if (rotation != other.rotation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = axis.hashCode()
        result = 31 * result + rotation
        return result
    }

    override fun toString(): String {
        return "${rotation.toString().replace("1", "")}$axis"
    }


}
