package advent.day.day06

import advent.AdventDay

class AdventDay06 : AdventDay() {
    private val fileText = getFileAsText("day06")
    private val initialStates = fileText.split(",").map { it.trim().toInt() }
    private var fishStates = LongArray(9) { i ->
        initialStates.count { i == it }.toLong()
    }

    override fun run() {
        (1..256).forEach { i ->
            val subArray = (1 until this.fishStates.size).map { this.fishStates[it] }.toLongArray()
            this.fishStates = subArray + this.fishStates[0]
            this.fishStates[6] += this.fishStates[8]

            println("After $i days: ${fishStates.sumOf { it }}")
        }
    }
}
