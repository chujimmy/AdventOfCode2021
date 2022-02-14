package advent.day.day08

import advent.AdventDay

class AdventDay08 : AdventDay() {
    private val fileText = getFileAsText("day08")
    private val line = fileText.split("\n")
        .map { it.replace(" | ", " ") }
        .map { it.split(" ") }
        .map { entry -> entry.map { it.toCharArray().toSet() } }
        .map { Pair(it.subList(0, 10).toSet(), it.subList(10, it.size).toList()) }

    override fun run() {
        val decodedNumbers = line.map { line ->
            val digits = line.first
            val output = line.second

            // 1, 4, 7 and 8 have a unique number of segments, so we can identify them
            val oneDisplay = digits.firstNotNullOf { it.takeIf { segments -> segments.size == 2 } }
            val fourDisplay = digits.firstNotNullOf { it.takeIf { segments -> segments.size == 4 } }
            val sevenDisplay = digits.firstNotNullOf { it.takeIf { segments -> segments.size == 3 } }
            val eightDisplay = digits.firstNotNullOf { it.takeIf { segments -> segments.size == 7 } }

            // We can now find 9, as it's the only six segments digit that overlaps with 4 and 7
            val nineDisplay = digits
                .filter { it.size == 6 }
                .filter { it.minus(fourDisplay).minus(sevenDisplay).size == 1 }[0]

            // We can now find 0, as among the 2 remaining digits with six segments, it's the one overlapping 7
            val zeroDisplay = digits
                .filter { it.size == 6 }
                .filter { it != nineDisplay }
                .filter { it.intersect(sevenDisplay).size == 3 }[0]

            // We now can find 6 as it's the only remaining digits with six segments
            val sixDisplay = digits
                .filter { it.size == 6 }
                .filter { it != zeroDisplay && it != nineDisplay }[0]

            val fiveDisplay = digits
                .filter { it.size == 5 }
                .filter { it.plus(sixDisplay) == sixDisplay }[0]

            val threeDisplay = digits
                .filter { it.size == 5 }
                .filter { it.intersect(oneDisplay) == oneDisplay }[0]

            val twoDisplay = digits
                .filter { it.size == 5 }
                .filter { it != fiveDisplay && it != threeDisplay }[0]

            val numbers = mapOf(
                zeroDisplay to "0",
                oneDisplay to "1",
                twoDisplay to "2",
                threeDisplay to "3",
                fourDisplay to "4",
                fiveDisplay to "5",
                sixDisplay to "6",
                sevenDisplay to "7",
                eightDisplay to "8",
                nineDisplay to "9",
            )

            output
                .map { numbers[it] }
                .joinToString("")
                .toInt()
        }

        val totalOneFourSevenEight = decodedNumbers.sumOf { it.toString().count { d -> d in setOf('1', '4', '7', '8') } }
        println("Total of 1, 4, 7 or 8: $totalOneFourSevenEight")

        val totalSum = decodedNumbers.sumOf { it }
        println("Total Sum: $totalSum")
    }
}
