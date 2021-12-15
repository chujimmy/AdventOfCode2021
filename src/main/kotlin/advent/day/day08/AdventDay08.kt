package advent.day.day08

import advent.AdventDay
import advent.day.day08.domain.SegmentsNumber

class AdventDay08 : AdventDay() {
    override fun run() {
        var total = 0
        val permutations = mutableMapOf<Char, Char>()
        val fileText = getFileAsText("day08")
        val io = fileText.split("\n").map { str ->
            val io = str.split(" | ")
            Pair(
                io[0].split(" ").toSet().map { it.toCharArray().toSet() },
                io[1].split(" ").toList().map { it.toCharArray().toSet() },
            )
        }

        io.forEach { ioPair ->
            val input = ioPair.first
            val output = ioPair.second

            // Those 4 digits have a unique number of segments so it's easy to find them
            val oneDisplay = input.firstNotNullOf { it.takeIf { segments -> segments.size == 2 } }
            val fourDisplay = input.firstNotNullOf { it.takeIf { segments -> segments.size == 4 } }
            val sevenDisplay = input.firstNotNullOf { it.takeIf { segments -> segments.size == 3 } }
            val eightDisplay = input.firstNotNullOf { it.takeIf { segments -> segments.size == 7 } }

            // We can find 9 as among digits with six segments
            // It's the only one that overlaps with 4 and and 7
            val nineDisplay = input
                .filter { it.size == 6 }
                .filter {
                    it.minus(fourDisplay).minus(sevenDisplay).size == 1
                }[0]

            val aPermutation = sevenDisplay.minus(oneDisplay).first()
            val gPermutation = nineDisplay.minus(fourDisplay).minus(aPermutation).first()
            val ePermutation = eightDisplay.minus(nineDisplay).first()

            // We now can find 0 as among the 2 remaining digits with six segments
            // It's the only one that overlaps with 7 (as the other win is 6)
            val zeroDisplay = input
                .filter { it.size == 6 }
                .filter { it.intersect(sevenDisplay) == sevenDisplay }
                .filter { it.contains(ePermutation) }[0]

            // We now can find 6 as it's the only remaining digits with six segments
            val sixDisplay = input
                .filter { it.size == 6 }
                .filter { it != zeroDisplay && it != nineDisplay }[0]

            // We now now calculate the permutation with that
            val dPermutation = eightDisplay.minus(zeroDisplay).first()
            val cPermutation = eightDisplay.minus(sixDisplay).first()
            val fPermutation = oneDisplay.minus(cPermutation).first()
            val bPermutation = fourDisplay.minus(oneDisplay).minus(dPermutation).first()

            permutations[aPermutation] = 'a'
            permutations[bPermutation] = 'b'
            permutations[cPermutation] = 'c'
            permutations[dPermutation] = 'd'
            permutations[ePermutation] = 'e'
            permutations[fPermutation] = 'f'
            permutations[gPermutation] = 'g'

            var outputStr = ""
            output.forEach { digit ->
                val segments = digit.mapNotNull { permutations[it] }.toSet()

                outputStr += SegmentsNumber.fromSegments(segments).charValue
            }

            total += outputStr.toInt()
            println("Digits: $outputStr")
        }

        println("Total: $total")
    }
}
