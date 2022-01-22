package advent.day.day19.domain

class Rotation(
    val xOrientation: Orientation,
    val xAxis: Axis,
    val yOrientation: Orientation,
    val yAxis: Axis,
    val zOrientation: Orientation,
    val zAxis: Axis,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rotation

        if (xOrientation != other.xOrientation) return false
        if (xAxis != other.xAxis) return false
        if (yOrientation != other.yOrientation) return false
        if (yAxis != other.yAxis) return false
        if (zOrientation != other.zOrientation) return false
        if (zAxis != other.zAxis) return false

        return true
    }

    override fun hashCode(): Int {
        var result = xOrientation.hashCode()
        result = 31 * result + xAxis.hashCode()
        result = 31 * result + yOrientation.hashCode()
        result = 31 * result + yAxis.hashCode()
        result = 31 * result + zOrientation.hashCode()
        result = 31 * result + zAxis.hashCode()
        return result
    }

    override fun toString(): String {
        return "(${xOrientation.display}${xAxis.axis}, ${yOrientation.display}${yAxis.axis}, ${zOrientation.display}${zAxis.axis})"
    }
}
