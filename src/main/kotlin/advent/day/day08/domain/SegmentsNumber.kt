package advent.day.day08.domain

enum class SegmentsNumber(val charValue: Char, val segments: Set<Char>) {
    ZERO('0', setOf('a', 'b', 'c', 'e', 'f', 'g')),
    ONE('1', setOf('c', 'f')),
    TWO('2', setOf('a', 'c', 'd', 'e', 'g')),
    THREE('3', setOf('a', 'c', 'd', 'f', 'g')),
    FOUR('4', setOf('b', 'c', 'd', 'f')),
    FIVE('5', setOf('a', 'b', 'd', 'f', 'g')),
    SIX('6', setOf('a', 'b', 'd', 'e', 'f', 'g')),
    SEVEN('7', setOf('a', 'c', 'f')),
    EIGHT('8', setOf('a', 'b', 'c', 'd', 'e', 'f', 'g')),
    NINE('9', setOf('a', 'b', 'c', 'd', 'f', 'g'));

    companion object {
        private val map = SegmentsNumber.values().associateBy(SegmentsNumber::segments)

        fun fromSegments(segments: Set<Char>): SegmentsNumber {
            return map.getOrDefault(segments, ZERO)
        }
    }
}
