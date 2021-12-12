package advent.day.day06.domain

class LanternfishStates(private val initialState: IntArray) {

    private var states = LongArray(9) { i ->
        initialState.count { a ->
            i == a
        }.toLong()
    }

    fun updateTimer() {
        val subArray = (1 until this.states.size).map { this.states[it] }.toLongArray()

        this.states = subArray + this.states[0]
        this.states[6] += this.states[8]
    }

    fun countLanternfishes(): Long {
        return this.states.sumOf { it }
    }
}
