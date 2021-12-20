package advent.day.day03

import advent.AdventDay

class AdventDay03 : AdventDay() {
    private val fileText = getFileAsText("day03")
    private val binaryNumbers = fileText.split("\n").toList()

    override fun run() {
        runPart01()
        runPart02()
    }

    private fun runPart01() {
        val bitOccurrences = countBitOccurrences(binaryNumbers)

        val gammaRateBinary = getBinaryNumber(bitOccurrences.first)
        val epsilonRateBinary = getBinaryNumber(bitOccurrences.second)

        println("Gamma Rate: $gammaRateBinary")
        println("Gamma Rate Value: ${Integer.parseInt(gammaRateBinary, 2)}")

        println("Epsilon Rate: $epsilonRateBinary")
        println("Epsilon Rate Value: ${Integer.parseInt(epsilonRateBinary, 2)}\n")
    }

    private fun runPart02() {
        val oxygenGeneratorRating = findRating(binaryNumbers, 0, true)
        val co2ScrubberRating = findRating(binaryNumbers, 0, false)

        val oxygenGeneratorRatingBinary = oxygenGeneratorRating.getOrElse(0) { "0" }
        val co2ScrubberRatingBinary = co2ScrubberRating.getOrElse(0) { "0" }

        println("Oxygen Generator Rating: $oxygenGeneratorRatingBinary")
        println("Oxygen Generator Rating Value: ${Integer.parseInt(oxygenGeneratorRatingBinary, 2)}")
        println("CO2 Scrubber Rating: $co2ScrubberRatingBinary")
        println("CO2 Scrubber Rating Value: ${Integer.parseInt(co2ScrubberRatingBinary, 2)}\n")
    }

    private fun getBinaryNumber(binaryNumber: Map<Int, Char>): String {
        return binaryNumber
            .toSortedMap()
            .map { it.value }
            .filterNotNull()
            .joinToString("")
    }

    private fun findRating(
        binaryNumbers: List<String>,
        bitIndexToCheck: Int,
        useMostCommonBit: Boolean
    ): List<String> {
        if (binaryNumbers.size <= 1) {
            return binaryNumbers
        }

        val filteredBinaryNumbers = binaryNumbers.filter { number ->
            val charToCheck = if (useMostCommonBit) {
                countBitOccurrences(binaryNumbers).first
            } else {
                countBitOccurrences(binaryNumbers).second
            }

            number[bitIndexToCheck] == charToCheck[bitIndexToCheck]
        }

        return findRating(filteredBinaryNumbers, bitIndexToCheck + 1, useMostCommonBit)
    }

    private fun countBitOccurrences(binaryNumbers: List<String>): Pair<Map<Int, Char>, Map<Int, Char>> {
        val bitsPerNumber = binaryNumbers[0].length
        val totalBinaryNumbers = binaryNumbers.size

        return (0 until bitsPerNumber).map { bitPosition ->
            val ones = binaryNumbers.count { it[bitPosition] == '1' }
            val mostCommonChar = if (ones * 2 >= totalBinaryNumbers) '1' else '0'
            val leastCommonChar = if (ones * 2 >= totalBinaryNumbers) '0' else '1'

            Pair(hashMapOf(bitPosition to mostCommonChar), hashMapOf(bitPosition to leastCommonChar))
        }.fold(Pair(emptyMap(), emptyMap())) { acc, pair ->
            Pair(acc.first.plus(pair.first), acc.second.plus(pair.second))
        }
    }
}
