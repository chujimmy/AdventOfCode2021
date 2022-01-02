package advent.day.day01

import advent.AdventDay

class AdventDay01 : AdventDay() {
    private val fileText = getFileAsText("day01")

    override fun run() {
        val measurementsValues = fileText
            .split("\n")
            .map { it.toInt() }

        val increasedValuePart01 = measurementsValues
            .windowed(1, 1)
            .windowed(2, 1)
            .count { it[0].sum() < it[1].sum() }

        val increasedValuePart02 = measurementsValues
            .windowed(3, 1)
            .windowed(2, 1)
            .count { it[0].sum() < it[1].sum() }

        println("Number of increase Part 01: $increasedValuePart01")
        println("Number of increase Part 02: $increasedValuePart02")
    }
}
