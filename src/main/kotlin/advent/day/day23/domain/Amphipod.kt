package advent.day.day23.domain

enum class Amphipod(val type: Char, val energy: Int) {
    A('A', 1),
    B('B', 10),
    C('C', 100),
    D('D', 1000);

    companion object {
        private val map = values().associateBy(Amphipod::type)

        fun fromType(type: Char): Amphipod {
            return map.getValue(type)
        }
    }
}
