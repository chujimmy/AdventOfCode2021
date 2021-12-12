package advent.day.day06

import advent.AdventDay
import advent.day.day06.domain.LanternfishStates

class AdventDay06 : AdventDay() {
    override fun run() {
        val fileText = getFileAsText("day06")
        val initialState = fileText.split(",")
            .map { it.trim().toInt() }
            .toIntArray()

        val lanternfishStates = LanternfishStates(initialState)

        (1..256).forEach { i ->
            lanternfishStates.updateTimer()

            println("After $i days: ${lanternfishStates.countLanternfishes()}")
        }
    }
}
