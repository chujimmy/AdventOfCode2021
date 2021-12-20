package advent.day.day02.domain

enum class Movement(
    val movementName: String,
    val multiplier: Int
) {
    UP("up", -1),
    DOWN("down", 1),
    FORWARD("forward", 1),
    NONE("", 0);

    companion object {
        private val map = values().associateBy(Movement::movementName)

        fun fromName(name: String): Movement {
            return map.getOrDefault(name, NONE)
        }
    }
}
