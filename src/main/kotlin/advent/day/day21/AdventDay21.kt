package advent.day.day21

import advent.AdventDay
import advent.day.day21.domain.PositionAndScore
import java.math.BigInteger

class AdventDay21 : AdventDay() {
    private val fileText = getFileAsText("day21")
    private val startingPositions = fileText.split("\n")
        .map { str -> Integer.parseInt(str.substring(28)) }
    private val player1 = PositionAndScore((startingPositions[0] - 1) % 10 + 1, 0)
    private val player2 = PositionAndScore((startingPositions[1] - 1) % 10 + 1, 0)

    private val minWinningScore = 21
    private val cache = hashMapOf<Pair<PositionAndScore, PositionAndScore>, Pair<BigInteger, BigInteger>>()

    override fun run() {
        runPart01()
        runPart02()
    }

    private fun runPart01() {
        val dice = (1..100).toList()
        val scores = hashMapOf(1 to player1, 2 to player2)

        var rollCount = 0
        while (scores.values.count { it.score >= 1000 } == 0) {
            for (player in 1..2) {
                val rollResult = (rollCount until rollCount + 3).sumOf { dice[(it % dice.size)] }
                val playerInfo = scores.getValue(player)
                val newPosition = (playerInfo.position + rollResult - 1) % 10 + 1
                val newScore = playerInfo.score + newPosition

                rollCount += 3
                scores[player] = PositionAndScore(newPosition, newScore)

                if (newScore >= 1000) {
                    break
                }
            }
        }

        println("score $scores. Dice rolls $rollCount")
    }

    private fun runPart02() {
        val startTime = System.currentTimeMillis()
        val winsCount = play(player2, player1)
        val endTime = System.currentTimeMillis()
        println("Execution time ${(endTime - startTime)}ms")

        println("Most wins: ${winsCount.first.max(winsCount.second)}")
    }

    private fun play(previousPlayer: PositionAndScore, currentPlayer: PositionAndScore): Pair<BigInteger, BigInteger> {
        if (previousPlayer.score >= minWinningScore) {
            return Pair(BigInteger.ONE, BigInteger.ZERO)
        }

        if (cache.containsKey(Pair(currentPlayer, previousPlayer))) {
            return cache.getValue(Pair(currentPlayer, previousPlayer))
        }

        val diceRollScoreOccurrences = mapOf(
            3 to 1L,
            4 to 3L,
            5 to 6L,
            6 to 7L,
            7 to 6L,
            8 to 3L,
            9 to 1L,
        )

        var currentPlayerWin = BigInteger.ZERO
        var previousPlayerWin = BigInteger.ZERO

        diceRollScoreOccurrences
            .forEach { diceRollScore ->
                val newPosition = ((currentPlayer.position + diceRollScore.key - 1) % 10 + 1)
                val newScore = currentPlayer.score + newPosition
                val updatedPreviousPlayer = PositionAndScore(newPosition, newScore)

                val playScores = play(updatedPreviousPlayer, previousPlayer)

                currentPlayerWin += (playScores.second * diceRollScore.value.toBigInteger())
                previousPlayerWin += (playScores.first * diceRollScore.value.toBigInteger())

                cache[Pair(currentPlayer, previousPlayer)] = Pair(currentPlayerWin, previousPlayerWin)
            }

        return Pair(currentPlayerWin, previousPlayerWin)
    }
}
