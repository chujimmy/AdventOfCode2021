package advent.day.day03

import advent.AdventDay

class AdventDay03 : AdventDay() {
    private val fileText = getFileAsText("day03")
    private val binaryNumbers = fileText.split("\n")

    override fun run() {
        runPart01()
        runPart02()
    }

    private fun runPart01() {
        val bitOccurrences = countBitOccurrences(binaryNumbers)

        val gammaRateBinary = getBinaryNumber(bitOccurrences.first)
        val gammaRate = Integer.parseInt(gammaRateBinary, 2)
        val epsilonRateBinary = getBinaryNumber(bitOccurrences.second)
        val epsilonRate = Integer.parseInt(epsilonRateBinary, 2)

        println("Gamma Rate\n\tBinary$gammaRateBinary\n\tValue: $gammaRate")
        println("Epsilon Rate\n\tBinary$epsilonRateBinary\n\tValue: $epsilonRate")
        println("Value: ${gammaRate * epsilonRate}\n")
    }

    private fun runPart02() {
        val oxygenGeneratorRating = findRating(binaryNumbers, 0, true)
        val co2ScrubberRating = findRating(binaryNumbers, 0, false)

        val oxygenGeneratorRatingBinary = oxygenGeneratorRating.getOrElse(0) { "0" }
        val oxygenGeneratorRatingInt = Integer.parseInt(oxygenGeneratorRatingBinary, 2)
        val co2ScrubberRatingBinary = co2ScrubberRating.getOrElse(0) { "0" }
        val co2ScrubberRatingInt = Integer.parseInt(co2ScrubberRatingBinary, 2)

        println("Oxygen Generator Rating\n\tBinary: $oxygenGeneratorRatingBinary\n\tValue: $oxygenGeneratorRatingInt")
        println("CO2 Scrubber Rating\n\tBinary: $co2ScrubberRatingBinary\n\tValue: $co2ScrubberRatingInt")
        println("Value: ${oxygenGeneratorRatingInt * co2ScrubberRatingInt}\n")
    }

    private fun getBinaryNumber(binaryNumber: Map<Int, Char>): String {
        return binaryNumber
            .toSortedMap()
            .mapNotNull { it.value }
            .joinToString("")
    }

    private fun findRating(
        binaryNumbers: List<String>,
        bitIndexToCheck: Int,
        useMostCommonBit: Boolean,
    ): List<String> {
        if (binaryNumbers.size <= 1) {
            return binaryNumbers
        }

        val filteredBinaryNumbers = binaryNumbers
            .filter { number ->
                val bitsCount = countBitOccurrences(binaryNumbers)
                val charToCheck = if (useMostCommonBit) bitsCount.first else bitsCount.second

                number[bitIndexToCheck] == charToCheck[bitIndexToCheck]
            }

        return findRating(filteredBinaryNumbers, bitIndexToCheck + 1, useMostCommonBit)
    }

    private fun countBitOccurrences(binaryNumbers: List<String>): Pair<Map<Int, Char>, Map<Int, Char>> {
        val bitsPerNumber = binaryNumbers[0].length
        val totalBinaryNumbers = binaryNumbers.size

        return (0 until bitsPerNumber)
            .map { bitPosition ->
                val ones = binaryNumbers.count { it[bitPosition] == '1' }
                val mostCommonChar = if (ones * 2 >= totalBinaryNumbers) '1' else '0'
                val leastCommonChar = if (ones * 2 >= totalBinaryNumbers) '0' else '1'

                Pair(hashMapOf(bitPosition to mostCommonChar), hashMapOf(bitPosition to leastCommonChar))
            }
            .fold(Pair(emptyMap(), emptyMap())) { acc, pair ->
                Pair(acc.first.plus(pair.first), acc.second.plus(pair.second))
            }
    }
}
