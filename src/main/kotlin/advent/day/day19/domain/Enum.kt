package advent.day.day19.domain

enum class Axis(val axis: String) {
    X("x"),
    Y("y"),
    Z("z");
}

enum class Orientation(val orientation: Int, val display: String) {
    POSITIVE(1, ""),
    NEGATIVE(-1, "-");
}
