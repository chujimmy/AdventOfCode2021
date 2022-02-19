package advent.day.day14

import advent.AdventDay
import java.util.regex.Pattern

class AdventDay14 : AdventDay() {
    private val fileContent = getFileAsText("day14")
        .split(Pattern.compile("\n\n"))

    private val initialPolymer = fileContent[0]

    private val polymerInsertion = fileContent[1]
        .split("\n")
        .associate { it.split(" -> ")[0] to it.split(" -> ")[1] }

    private val polymerInitialCount = initialPolymer
        .windowed(2, 1)
        .groupBy { it }
        .mapValues { entry -> entry.value.size.toLong() }

    override fun run() {
        runPart01()
        runPart02()
    }

    private fun runPart01() {
        val polymer = insertPolymer(10)
        val elementDifference = countElementDifference(polymer)
        println("Most common element minus least common Element: $elementDifference")
    }

    private fun runPart02() {
        val polymer = insertPolymer(40)
        val elementDifference = countElementDifference(polymer)
        println("Most common element minus least common Element: $elementDifference")
    }

    private fun insertPolymer(steps: Int): Map<String, Long> {
        return (1..steps).fold(polymerInitialCount) { acc, _ ->
            acc
                .flatMap { entry ->
                    val polymerPair = entry.key
                    val count = entry.value

                    "${polymerPair.first()}${polymerInsertion[polymerPair]}${polymerPair.last()}"
                        .windowed(2, 1)
                        .map { Pair(it, count) }
                }
                .groupBy { it.first }
                .mapValues { entry -> entry.value.sumOf { it.second } }
        }
    }

    private fun countElementDifference(polymer: Map<String, Long>): Long {
        val polymerLastElement = initialPolymer[initialPolymer.lastIndex]
        val sortedElementCount = polymer
            .map { Pair(it.key[0], it.value) }
            .groupBy { it.first }
            .mapValues { e -> e.value.sumOf { it.second } }
            .mapValues { e -> if (e.key == polymerLastElement) e.value + 1 else e.value }
            .values
            .sortedByDescending { it }

        return sortedElementCount.first() - sortedElementCount.last()
    }
}
