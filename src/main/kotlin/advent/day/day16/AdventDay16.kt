package advent.day.day16

import advent.AdventDay
import java.math.BigInteger

class AdventDay16 : AdventDay() {
    private val fileContent = getFileAsText("day16")

    private val stringToDecode = fileContent
        .toCharArray()
        .joinToString("") { it.digitToInt(16).toString(2).padStart(4, '0') }

    override fun run() {
        println("Initial string: $fileContent")
        println("String to decode: $stringToDecode")

        val sums = sumPacketVersionsAndValues(StringBuilder(stringToDecode), 1) { a: BigInteger, b: BigInteger -> a + b }
        println("Versions sum: ${sums.first}")
        println("Values operation result: ${sums.second}")
    }

    private fun sumPacketVersionsAndValues(
        transmission: StringBuilder,
        packetToRead: Int,
        predicate: (BigInteger, BigInteger) -> (BigInteger)
    ): Pair<BigInteger, BigInteger> {
        var packetRead = 0
        var transmissionBuilder = transmission
        var summedVersionTotal = BigInteger.ZERO
        val valuesList = mutableListOf<BigInteger>()

        while (canReadPacket(transmissionBuilder, packetToRead, packetRead)) {
            val version = BigInteger(transmissionBuilder.substring(0, 3), 2)
            val type = Integer.parseInt(transmissionBuilder.substring(3, 6), 2)

            summedVersionTotal += version
            transmissionBuilder = transmissionBuilder.delete(0, 6)

            if (type == 4) {
                val literalBlocks = transmissionBuilder.toString().windowed(5, 5)
                val literalBlocksLastBlockIndex = literalBlocks.indexOfFirst { it.startsWith("0") }

                val literalValueBits = literalBlocks.subList(0, literalBlocksLastBlockIndex + 1)
                val literalValueBinary = literalValueBits.joinToString("") { it.substring(1) }

                transmissionBuilder = transmissionBuilder.delete(0, literalValueBits.sumOf { it.length })

                valuesList.add(BigInteger(literalValueBinary, 2))
            } else {
                var predicateToUse: (BigInteger, BigInteger) -> BigInteger = { a: BigInteger, b: BigInteger -> a + b }
                val triple = extractSubPacketToRead(transmissionBuilder)
                val subPacketToRead = triple.third
                val subPacketBits = triple.second
                transmissionBuilder = triple.first

                when (type) {
                    0 -> { predicateToUse = { a: BigInteger, b: BigInteger -> a + b } }
                    1 -> { predicateToUse = { a: BigInteger, b: BigInteger -> a * b } }
                    2 -> { predicateToUse = { a: BigInteger, b: BigInteger -> a.min(b) } }
                    3 -> { predicateToUse = { a: BigInteger, b: BigInteger -> a.max(b) } }
                    5 -> { predicateToUse = { a: BigInteger, b: BigInteger -> if (a > b) BigInteger.ONE else BigInteger.ZERO } }
                    6 -> { predicateToUse = { a: BigInteger, b: BigInteger -> if (a < b) BigInteger.ONE else BigInteger.ZERO } }
                    7 -> { predicateToUse = { a: BigInteger, b: BigInteger -> if (a == b) BigInteger.ONE else BigInteger.ZERO } }
                }

                val subPacketVersionsAndValuesSum = sumPacketVersionsAndValues(subPacketBits, subPacketToRead, predicateToUse)

                summedVersionTotal += subPacketVersionsAndValuesSum.first
                valuesList.add(subPacketVersionsAndValuesSum.second)
            }
            packetRead++
        }

        return Pair(summedVersionTotal, valuesList.reduce(predicate))
    }

    private fun extractSubPacketToRead(transmissionBuilder: StringBuilder): Triple<StringBuilder, StringBuilder, Int> {
        var builder = transmissionBuilder
        val packetLengthTypeId = builder[0].digitToInt(2)
        builder = builder.delete(0, 1)

        return if (packetLengthTypeId == 0) {
            val bitsToReadCount = Integer.parseInt(builder.substring(0, 15), 2)
            builder = builder.delete(0, 15)

            val bitsToRead = StringBuilder(builder.substring(0, bitsToReadCount))
            builder = builder.delete(0, bitsToReadCount)

            Triple(builder, bitsToRead, -1)
        } else {
            val packetsToReadCount = Integer.parseInt(builder.substring(0, 11), 2)
            builder = builder.delete(0, 11)

            Triple(builder, builder, packetsToReadCount)
        }
    }

    private fun canReadPacket(builder: StringBuilder, packetToRead: Int, packetRead: Int): Boolean {
        return (packetToRead > 0 && packetRead < packetToRead) ||
            (packetToRead == -1 && builder.isNotBlank() && BigInteger(builder.toString(), 2) != BigInteger.ZERO)
    }
}
