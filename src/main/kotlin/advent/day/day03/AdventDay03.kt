package advent.day.day03

import advent.AdventDay

class AdventDay03: AdventDay() {
    override fun run() {
        val fileText = getFileAsText("day03")
        val binaryNumbers: List<String> = fileText.split("\n").toList()
        val bitCount = binaryNumbers[0].length

        val oxygenGeneratorRating = findRating(binaryNumbers, bitCount, 0, true)
        val co2ScrubberRating = findRating(binaryNumbers, bitCount, 0, false)

        val oxygenGeneratorRatingBinary = if (oxygenGeneratorRating.isNotEmpty()) oxygenGeneratorRating[0] else "0"
        val co2ScrubberRatingBinary = if (co2ScrubberRating.isNotEmpty()) co2ScrubberRating[0] else "0"

        println("Oxygen Generator Rating: $oxygenGeneratorRatingBinary")
        println("Oxygen Generator Rating Value: ${Integer.parseInt(oxygenGeneratorRatingBinary, 2)}")
        println("CO2 Scrubber Rating: $co2ScrubberRatingBinary")
        println("CO2 Scrubber Rating Value: ${Integer.parseInt(co2ScrubberRatingBinary, 2)}")
    }


    private fun findRating(
        binaryNumbers: List<String>,
        bitCount: Int,
        bitPosition: Int,
        useMostCommonOccurrence: Boolean
    ): List<String> {
        if (binaryNumbers.size == 1) {
            return binaryNumbers
        }

        if (bitPosition >= bitCount || binaryNumbers.isEmpty()) {
            return emptyList()
        }

        val bitsOccurrenceCount = countBitsOccurrence(binaryNumbers, bitCount)
        val bitsOccurrence = if (useMostCommonOccurrence) {
            bitsOccurrenceCount.first
        } else {
            bitsOccurrenceCount.second
        }

        val filteredBinaryNumbers = binaryNumbers.filter{
            it[bitPosition] == bitsOccurrence[bitPosition]
        }

        return findRating(filteredBinaryNumbers, bitCount, bitPosition + 1, useMostCommonOccurrence)
    }

    private fun countBitsOccurrence(
        binaryNumbers: List<String>,
        bitsNumber: Int,
    ): Pair<Map<Int, Char>, Map<Int, Char>> {
        if (binaryNumbers.isEmpty()) {
            val emptyMap = emptyMap<Int, Char>()
            return Pair(emptyMap, emptyMap)
        }

        val bitsOccurrence: MutableMap<Int, Int> = HashMap()

        val totalReadings = binaryNumbers.size

        for (binaryNumber in binaryNumbers) {
            for (i in 0 until bitsNumber) {
                if (binaryNumber[i] == '1') {
                    bitsOccurrence[i] = bitsOccurrence.getOrDefault(i, 0) + 1
                }
            }
        }

        val binaryNumberWithAllMostCommonBits = bitsOccurrence.mapValues {
            if ((it.value * 2) >= totalReadings) '1' else '0'
        }
        val binaryNumberWithLeastMostCommonBits = binaryNumberWithAllMostCommonBits.mapValues{
            if (it.value == '1') {
                '0'
            } else {
                '1'
            }
        }

        return Pair(binaryNumberWithAllMostCommonBits, binaryNumberWithLeastMostCommonBits)
    }
}