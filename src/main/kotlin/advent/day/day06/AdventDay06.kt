package advent.day.day06

import advent.AdventDay

class AdventDay06 : AdventDay() {
    private val fileText = getFileAsText("day06")
    private val initialStates = fileText.split(",").map { it.trim().toInt() }
    private val fishStates = LongArray(9) { i -> initialStates.count { i == it }.toLong() }

    override fun run() {
        runPart01()
        runPart02()
    }

    private fun runPart01() {
        println("Total fish: ${countTotalFish(fishStates, 80)}")
    }

    private fun runPart02() {
        println("Total fish: ${countTotalFish(fishStates, 256)}")
    }

    private fun countTotalFish(initialFishStates: LongArray, days: Int): Long {
        return (1..days)
            .toList()
            .fold(initialFishStates) { fishStates, _ ->
                fishStates
                    .copyOfRange(1, 7)
                    .plus(fishStates[7] + fishStates[0])
                    .plus(fishStates.copyOfRange(8, fishStates.lastIndex + 1))
                    .plus(fishStates[0])
            }
            .sumOf { it }
    }
}
