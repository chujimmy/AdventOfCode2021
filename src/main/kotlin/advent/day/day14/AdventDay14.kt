package advent.day.day14

import advent.AdventDay
import java.util.regex.Pattern

class AdventDay14 : AdventDay() {
    private val fileContent = getFileAsText("day14").split(Pattern.compile("\n\n"))

    override fun run() {
        val initialPolymer = fileContent[0]
        val polymerInsertion = fileContent[1]
            .split("\n")
            .associate { it.split(" -> ")[0] to it.split(" -> ")[1] }

        val polymerLastElement = initialPolymer[initialPolymer.lastIndex]
        val polymerInitialCount = initialPolymer
            .windowed(2, 1)
            .groupBy { it }
            .mapValues { entry -> entry.value.size.toLong() }

        (1..40).fold(polymerInitialCount) { acc, i ->
            val polymerCountWithInsert = acc
                .flatMap { entry ->
                    val polymerPair = entry.key
                    val count = entry.value

                    "${polymerPair.first()}${polymerInsertion[polymerPair]}${polymerPair.last()}"
                        .windowed(2, 1)
                        .map { Pair(it, count) }
                }
                .groupBy { it.first }
                .mapValues { entry -> entry.value.sumOf { it.second } }

            val sortedElementCount = polymerCountWithInsert
                .map { Pair(it.key[0], it.value) }
                .groupBy { it.first }
                .mapValues { e ->
                    val elementSum = e.value.sumOf { it.second }
                    if (e.key == polymerLastElement) elementSum + 1 else elementSum
                }
                .values
                .sortedByDescending { it }

            println("Step $i Value: ${sortedElementCount.first() - sortedElementCount.last()}")

            polymerCountWithInsert
        }
    }
}
