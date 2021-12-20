package advent.day.day14

import advent.AdventDay
import java.math.BigInteger
import java.util.regex.Pattern

class AdventDay14 : AdventDay() {
    override fun run() {
        val fileContent = getFileAsText("day14")
            .split(Pattern.compile("\n\n"))

        if (fileContent.size < 2) {
            return
        }

        val polymer = fileContent[0]
        val pairInsertion = fileContent[1]
            .split("\n")
            .associate { it.split(" -> ")[0] to it.split(" -> ")[1] }

        var pairCount: Map<String, BigInteger> = polymer
            .windowed(2, 1)
            .groupingBy { it }
            .fold(BigInteger.ZERO) { accumulator, _ -> accumulator.add(BigInteger.ONE) }

        (1..40).forEach { i ->
            pairCount = pairCount
                .map { entry ->
                    val pair = entry.key
                    val count = entry.value

                    "${pair.substring(0, 1)}${pairInsertion[pair]}${pair.substring(1)}"
                        .windowed(2, 1)
                        .associateWith { count }
                }.fold(emptyMap()) { acc, map ->
                    acc
                        .keys.union(map.keys)
                        .associateWith {
                            acc.getOrDefault(it, BigInteger.ZERO).add(map.getOrDefault(it, BigInteger.ZERO))
                        }
                }

            val elementCount = pairCount
                .map { Pair(it.key[0], it.value) }
                .groupingBy { it.first }
                .fold(BigInteger.ZERO) { acc, pair -> acc.add(pair.second) }
                .toMutableMap()

            val lastElement = polymer[polymer.lastIndex]
            elementCount[lastElement] = elementCount.getOrDefault(lastElement, BigInteger.ZERO).add(BigInteger.ONE)

            val sortedElement = elementCount
                .values
                .sortedBy { it.times(BigInteger.valueOf(-1)) }

            println("Step $i Value: ${sortedElement.first() - sortedElement.last()}")
        }
    }
}
