package advent.day.day16

import advent.AdventDay
import java.lang.Long.max
import java.lang.Long.min
import java.math.BigInteger

class AdventDay16 : AdventDay() {
    private val fileContent = getFileAsText("day16")

    private val stringToDecode = fileContent
        .toCharArray()
        .joinToString("") { it.digitToInt(16).toString(2).padStart(4, '0') }

    override fun run() {
        println("Initial string: $fileContent")
        println("String to decode: $stringToDecode")

        val sums = sumPacketVersionsAndValues(StringBuilder(stringToDecode), 1) { a, b -> a + b }
        println("Versions sum: ${sums.first}")
        println("Values operation result: ${sums.second}")
    }

    private fun sumPacketVersionsAndValues(
        transmission: StringBuilder,
        packetToRead: Int,
        predicate: (Long, Long) -> (Long)
    ): Pair<Long, Long> {
        var packetRead = 0
        var summedVersionTotal = 0L
        val valuesList = mutableListOf<Long>()

        while (canReadPacket(transmission, packetToRead, packetRead)) {
            val version = extractFromBuilder(transmission, 3).toInt(2)
            val type = extractFromBuilder(transmission, 3).toInt(2)

            summedVersionTotal += version

            if (type == 4) {
                val literalBlocks = transmission.toString().windowed(5, 5)
                val literalBlocksLastBlockIndex = literalBlocks.indexOfFirst { it.startsWith("0") }

                val literalValueBits = literalBlocks.subList(0, literalBlocksLastBlockIndex + 1)
                val literalValueBinary = literalValueBits.joinToString("") { it.substring(1) }

                transmission.delete(0, literalValueBits.sumOf { it.length })

                valuesList.add(literalValueBinary.toLong(2))
            } else {
                val pair = extractSubPacketToRead(transmission)
                val subPacketToRead = pair.second
                val subPacketBits = pair.first

                val predicateToUse = when (type) {
                    0 -> { { a: Long, b: Long -> a + b } }
                    1 -> { { a: Long, b: Long -> a * b } }
                    2 -> { { a: Long, b: Long -> min(a, b) } }
                    3 -> { { a: Long, b: Long -> max(a, b) } }
                    5 -> { { a: Long, b: Long -> if (a > b) 1L else 0L } }
                    6 -> { { a: Long, b: Long -> if (a < b) 1L else 0L } }
                    7 -> { { a: Long, b: Long -> if (a == b) 1L else 0L } }
                    else -> { { a: Long, b: Long -> a + b } }
                }

                val subPacketVersionsAndValuesSum = sumPacketVersionsAndValues(subPacketBits, subPacketToRead, predicateToUse)

                summedVersionTotal += subPacketVersionsAndValuesSum.first
                valuesList.add(subPacketVersionsAndValuesSum.second)
            }
            packetRead++
        }

        return Pair(summedVersionTotal, valuesList.reduce(predicate))
    }

    private fun extractSubPacketToRead(transmissionBuilder: StringBuilder): Pair<StringBuilder, Int> {
        val packetLengthTypeId = extractFromBuilder(transmissionBuilder, 1).toInt(2)

        return if (packetLengthTypeId == 0) {
            val bitsToReadCount = extractFromBuilder(transmissionBuilder, 15).toInt(2)
            val bitsToRead = StringBuilder(extractFromBuilder(transmissionBuilder, bitsToReadCount))

            Pair(bitsToRead, -1)
        } else {
            val packetsToReadCount = extractFromBuilder(transmissionBuilder, 11).toInt(2)

            Pair(transmissionBuilder, packetsToReadCount)
        }
    }

    private fun canReadPacket(builder: StringBuilder, packetToRead: Int, packetRead: Int): Boolean {
        return (packetToRead > 0 && packetRead < packetToRead) ||
            (packetToRead == -1 && builder.isNotBlank() && BigInteger(builder.toString(), 2) != BigInteger.ZERO)
    }

    private fun extractFromBuilder(builder: StringBuilder, end: Int): String {
        val str = builder.substring(0, end)
        builder.delete(0, end)
        return str
    }
}
